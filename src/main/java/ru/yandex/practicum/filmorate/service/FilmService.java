package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addLike(int userId, int filmId) {
        log.info("Начинается добавление лайка фильму");
        User user = userStorage.findUser(userId);
        log.trace("по заданному id был найден пользователь");
        Film film = filmStorage.findFilm(filmId);
        log.trace("по заданному id был найден фильм");

        if (!film.addLike(userId)) {
            log.warn("Данный пользователь уже ставил лайк этому фильму");
            throw new ConditionsNotMetException("Данный пользователь уже ставил лайк этому фильму");
        }
        log.info("Фильму успешно был поставлен лайк: {}", film);

        return film;
    }

    public Film deleteLike(int userId, int filmId) {
        log.info("Начинается удаление лайка фильму");
        User user = userStorage.findUser(userId);
        log.trace("по заданному id был найден пользователь");
        Film film = filmStorage.findFilm(filmId);
        log.trace("по заданному id был найден фильм");

        if (!film.deleteLike(userId)) {
            log.warn("Данный пользователь не ставил лайк этому фильму");
            throw new ConditionsNotMetException("Данный пользователь не ставил лайк этому фильму");
        }
        log.info("У фильма успешно был удален лайк: {}", film);

        return film;
    }

    public Collection<Film> topLikedFilms(int filmsLimit) {
        log.info("Происходит поиск {} самых залайканных фильмов", filmsLimit);
        return filmStorage.findAll().stream()
                .sorted((film, film2) -> film2.getLikes().size() - film.getLikes().size())
                .limit(filmsLimit)
                .collect(Collectors.toList());
    }
}
