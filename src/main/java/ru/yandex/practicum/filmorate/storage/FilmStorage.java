package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAll();

    Film findFilm(int id);

    Film createFilm(@RequestBody Film film);

    Film updateFilm(@RequestBody Film film);
}
