package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@RequiredArgsConstructor
@Component
public class MiscDbStorage implements MiscStorage {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper genreMapper;
    private final MpaRowMapper mpaMapper;

    public Collection<Genre> findAllGenres() {
        String findGenres = "SELECT * " +
                "FROM category ";

        return jdbc.query(findGenres, genreMapper);
    }

    public Genre findGenre(int genreId) {
        String findGenre = "SELECT * " +
                "FROM category " +
                "WHERE id = ?";

        try {
            return jdbc.queryForObject(findGenre, genreMapper, genreId);
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Жанр с таким id не был найден");
        }
    }

    public Collection<Mpa> findAllMpa() {
        String findMpa = "SELECT * " +
                "FROM age_rating ";

        return jdbc.query(findMpa, mpaMapper);
    }

    public Mpa findMpa(int mpaId) {
        String findMpa = "SELECT * " +
                "FROM age_rating " +
                "WHERE id = ?";

        try {
            return jdbc.queryForObject(findMpa, mpaMapper, mpaId);
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Возрастной рейтинг с таким id не был найден");
        }
    }
}
