package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films;
    private int lastId;

    public InMemoryFilmStorage(Map<Integer, Film> films) {
        this.films = films;
        lastId = 0;
    }

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film findFilm(int id) {
        Film film = films.get(id);

        if (film == null) {
            log.warn("По заданному id фильм не был найден");
            throw new NotFoundException("Фильм с таким id не был найден");
        }

        return film;
    }

    public Film createFilm(@RequestBody Film film) {
        log.info("Начинается добавление фильма");
        ConditionsNotMetException validateException = validateFilm(film);
        if (validateException != null) {
            log.warn("Ошибка валидации при добавлении фильма", validateException);
            throw validateException;
        }
        log.trace("Фильм прошел валидацию");

        Film newFilm = new Film(film, createFilmId());
        log.debug("Была создана копия фильма с добавлением id: {}", newFilm);
        films.put(newFilm.getId(), newFilm);
        log.trace("Копия была добавлен в список");

        log.info("Добавление фильма прошло успешно");
        return newFilm;
    }

    public Film updateFilm(@RequestBody Film film) {
        log.info("Начинается обновление данных фильма");
        if (!films.containsKey(film.getId())) {
            log.warn("Не был найден фильм по заданному id, при обновлении данных");
            throw new NotFoundException("Фильм с таким id не был найден");
        }
        log.trace("Фильм с заданным id был найден");

        Film oldFilm = films.get(film.getId());
        Film updatedFilm = new Film(oldFilm, oldFilm.getId());
        log.debug("Была создана копия старого варианта фильма: {}", updatedFilm);

        if (film.getReleaseDate() != null) {
            updatedFilm.setReleaseDate(film.getReleaseDate());
        }
        if (film.getName() != null) {
            updatedFilm.setName(film.getName());
        }
        if (film.getDescription() != null) {
            updatedFilm.setDescription(film.getDescription());
        }
        if (film.getDuration() != null) {
            updatedFilm.setDuration(film.getDuration());
        }
        oldFilm.getLikes().forEach(updatedFilm::addLike);
        log.debug("В копию были добавлены измененные данные: {}", updatedFilm);

        ConditionsNotMetException validateException = validateFilm(updatedFilm);
        if (validateException != null) {
            log.warn("Ошибка валидации при обновлении содержимого фильма", validateException);
            throw validateException;
        }
        log.trace("Измененная копия фильма прошла валидацию");

        films.put(updatedFilm.getId(), updatedFilm);
        log.info("Обновление данных фильма прошло успешно");
        return updatedFilm;
    }

    private int createFilmId() {
        lastId++;
        return lastId;
    }

    public ConditionsNotMetException validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            return new ConditionsNotMetException("Название фильма не должно быть пустым");
        }
        if (film.getDescription().length() > 200) {
            return new ConditionsNotMetException("Описание не должно содержать больше 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            return new ConditionsNotMetException("Дата релиза не может быть раньше дня рождения кино");
        }
        if (!film.getDuration().isPositive()) {
            return new ConditionsNotMetException("Продолжительность фильма не может быть отрицательной или равна нулю");
        }
        return null;
    }
}
