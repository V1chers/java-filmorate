package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmMapper;
    private final GenreRowMapper genreMapper;

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper filmMapper, GenreRowMapper genreMapper) {
        this.jdbc = jdbc;
        this.filmMapper = filmMapper;
        this.genreMapper = genreMapper;
    }

    public Collection<Film> findAll() {
        String findFilm = "SELECT id, " +
                "name, " +
                "description, " +
                "release_date, " +
                "duration, " +
                "age_rating " +
                "FROM films ";

        Collection<Film> films = jdbc.query(findFilm, filmMapper);

        films.forEach(film -> {
            Set<Genre> filmGenres = getGenres(film.getId());
            Set<Integer> filmLikes = getLikes(film.getId());

            film.addGenres(filmGenres);
            film.addLikes(filmLikes);

            film.getMpa().setName(findAgeRating(film));
        });

        return films;
    }

    public Film findFilm(int filmId) {
        String findFilm = "SELECT id, " +
                "name, " +
                "description, " +
                "release_date, " +
                "duration, " +
                "age_rating " +
                "FROM films " +
                "WHERE id = ?";

        try {
            Film film = jdbc.queryForObject(findFilm, filmMapper, filmId);

            Set<Genre> filmGenres = getGenres(filmId);
            Set<Integer> filmLikes = getLikes(filmId);

            film.addGenres(filmGenres);
            film.addLikes(filmLikes);

            film.getMpa().setName(findAgeRating(film));

            return film;
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Фильм с таким id не был найден");
        }
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        checkFilmsFields(film);

        Date releaseDay = film.getReleaseDate() == null ? null : Date.valueOf(film.getReleaseDate());
        Integer mpaId = film.getMpa() == null ? null : film.getMpa().getId();

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String insertFilm = "INSERT INTO films(name, description, release_date, duration, age_rating)" +
                "VALUES(?, ?, ?, ?, ?)";

        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(insertFilm, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, releaseDay);
            ps.setInt(4, (int) film.getDuration().toMinutes());
            if (mpaId == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, mpaId);
            }
            return ps;
        }, keyHolder);

        Film filmWithId = new Film(film, keyHolder.getKeyAs(Integer.class));

        updateGenres(filmWithId);
        updateLikes(filmWithId);

        return filmWithId;
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        checkFilmsFields(film);

        String updateFilm = "UPDATE films SET name = ?, " +
                "description = ?, " +
                "release_date = ?, " +
                "duration = ?, " +
                "age_rating = ? " +
                "WHERE id = ?";

        int rowsUpdated = jdbc.update(updateFilm, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration().toMinutes(), film.getMpa().getId(), film.getId());

        if (rowsUpdated == 0) {
            throw new NotFoundException("Такого фильма не существует");
        }

        updateGenres(film);
        updateLikes(film);

        return film;
    }

    private void updateGenres(Film film) {
        Set<Integer> newGenres = film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());
        Set<Integer> oldGenres = getGenres(film.getId()).stream().map(Genre::getId).collect(Collectors.toSet());

        HashMap<String, Set<Integer>> ChangeDataList = makeChangeDataList(newGenres, oldGenres);

        addGenres(ChangeDataList.get("toAdd"), film.getId());
        deleteGenres(ChangeDataList.get("toDelete"), film.getId());
    }

    private void updateLikes(Film film) {
        Set<Integer> newLikes = film.getLikes();
        Set<Integer> oldLikes = getLikes(film.getId());

        HashMap<String, Set<Integer>> changeDataList = makeChangeDataList(newLikes, oldLikes);

        addLikes(changeDataList.get("toAdd"), film.getId());
        deleteLikes(changeDataList.get("toDelete"), film.getId());
    }

    private HashMap<String, Set<Integer>> makeChangeDataList(Set<Integer> newData, Set<Integer> oldDate) {
        Set<Integer> listToDelete = new HashSet<>();
        Set<Integer> listToAdd = new HashSet<>();

        newData.forEach(userId -> {
            if (!oldDate.contains(userId)) {
                listToAdd.add(userId);
            }
        });
        oldDate.forEach(userId -> {
            if (!newData.contains(userId)) {
                listToDelete.add(userId);
            }
        });

        HashMap<String, Set<Integer>> changeDataList = new HashMap<>();
        changeDataList.put("toAdd", listToAdd);
        changeDataList.put("toDelete", listToDelete);

        return changeDataList;
    }

    private Set<Integer> getLikes(int filmId) {
        String queryLikes = "SELECT user_id " +
                "FROM films_likes " +
                "WHERE FILM_ID = ? ";

        try {
            return new HashSet<>(jdbc.queryForList(queryLikes, Integer.class, filmId));
        } catch (EmptyResultDataAccessException ignored) {
            return new HashSet<>();
        }
    }

    private Set<Genre> getGenres(int filmId) {
        String queryGenres = "SELECT c.id, c.category_name " +
                "FROM films_category AS fc " +
                "JOIN category AS c ON c.id = fc.category_id " +
                "WHERE fc.film_id = ? ";

        try {
            return new HashSet<>(jdbc.query(queryGenres, genreMapper, filmId));
        } catch (EmptyResultDataAccessException ignored) {
            return new HashSet<>();
        }
    }

    private void addGenres(Set<Integer> genres, int filmId) {
        String insertFilmGenres = "INSERT INTO films_category(film_id, category_id)" +
                "VALUES(?, ?)";

        genres.forEach(genreId -> jdbc.update(insertFilmGenres, filmId, genreId));
    }

    private void deleteGenres(Set<Integer> genres, int filmId) {
        String deleteFriend = "DELETE FROM films_category " +
                "WHERE film_id = ? " +
                "AND category_id = ? ";

        genres.forEach(genreId -> jdbc.update(deleteFriend, filmId, genreId));
    }

    private void addLikes(Set<Integer> usersId, int filmId) {
        String insertLikes = "INSERT INTO films_likes(film_id, user_id)" +
                "VALUES(?, ?)";

        usersId.forEach(userId -> jdbc.update(insertLikes, filmId, userId));
    }

    private void deleteLikes(Set<Integer> usersId, int filmId) {
        String deleteLikes = "DELETE FROM films_likes " +
                "WHERE film_id = ? " +
                "AND user_id = ? ";

        usersId.forEach(userId -> jdbc.update(deleteLikes, filmId, userId));
    }

    private String findAgeRating(Film film) {
        String queryGenres = "SELECT rating " +
                "FROM age_rating " +
                "WHERE id = ? ";

        int mpaId = film.getMpa().getId();
        if (mpaId == 0) {
            return null;
        }

        try {
            return jdbc.queryForObject(queryGenres, String.class, mpaId);
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Возрастного рейтинга с таким id не было найдено");
        }
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ConditionsNotMetException("Название фильма не должно быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ConditionsNotMetException("Описание не должно содержать больше 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            throw new ConditionsNotMetException("Дата релиза не может быть раньше дня рождения кино");
        }
        if (!film.getDuration().isPositive()) {
            throw new ConditionsNotMetException("Продолжительность фильма не может быть отрицательной или равна нулю");
        }
    }

    private void checkFilmsFields(Film film) {
        findAgeRating(film);

        String findGenre = "SELECT id " +
                "FROM  category " +
                "WHERE id = ? ";

        film.getGenres().forEach(genre -> {
            try {
                jdbc.queryForObject(findGenre, Integer.class, genre.getId());
            } catch (EmptyResultDataAccessException ignored) {
                throw new NotFoundException("Жанра с таким id не было найдено");
            }
        });

        String findUser = "SELECT id " +
                "FROM  users " +
                "WHERE id = ? ";

        film.getLikes().forEach(userID -> {
            try {
                jdbc.queryForObject(findUser, Integer.class, userID);
            } catch (EmptyResultDataAccessException ignored) {
                throw new NotFoundException("Пользователя поставившего лайк с таким id не было найдено");
            }
        });
    }
}
