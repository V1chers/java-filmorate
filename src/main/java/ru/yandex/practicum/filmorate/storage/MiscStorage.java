package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MiscStorage {
    Collection<Genre> findAllGenres();

    Genre findGenre(int genreId);

    Collection<Mpa> findAllMpa();

    Mpa findMpa(int mpaId);
}
