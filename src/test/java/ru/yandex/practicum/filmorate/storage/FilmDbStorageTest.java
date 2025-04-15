package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class})
public class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;

    @Test
    public void shouldCreateAndGetFilm() {
        Film film = createFilm();

        int createdFilmId = filmDbStorage.createFilm(film).getId();

        Film expectedFilm = new Film(film, createdFilmId);
        expectedFilm.getMpa().setName("G");

        assertEquals(expectedFilm, filmDbStorage.findFilm(createdFilmId));
    }

    @Test
    public void shouldUpdateAllFields() {
        Film film = filmDbStorage.findFilm(1);

        film.setName("123");
        film.setDescription("123");
        film.setDuration(123);
        film.setReleaseDate(LocalDate.of(1999, 1, 1));
        film.addLike(2);
        film.deleteLike(3);

        Genre genre = new Genre();
        genre.setId(2);
        genre.setName("Драма");
        film.addGenre(genre);

        Mpa mpa = new Mpa();
        mpa.setId(3);
        film.setMpa(mpa);
        film.getMpa().setName("PG-13");

        filmDbStorage.updateFilm(film);

        assertEquals(film, filmDbStorage.findFilm(1));
    }

    @Test
    public void shouldNotUpdateUnknownFilm() {
        Film film = createFilm();

        assertThrows(NotFoundException.class, () -> filmDbStorage.updateFilm(film));
    }

    @Test
    public void shouldNotAddUnknownCategory() {
        Film film = createFilm();
        Genre genre = new Genre();
        genre.setId(165);
        film.addGenre(genre);

        assertThrows(NotFoundException.class, () -> filmDbStorage.createFilm(film));
    }

    @Test
    public void shouldNotAddUnknownMpa() {
        Film film = createFilm();
        Mpa mpa = new Mpa();
        mpa.setId(165);
        film.setMpa(mpa);

        assertThrows(NotFoundException.class, () -> filmDbStorage.createFilm(film));
    }

    @Test
    public void shouldNotAddUserLike() {
        Film film = createFilm();
        film.addLike(123);

        assertThrows(NotFoundException.class, () -> filmDbStorage.createFilm(film));
    }

    private Film createFilm() {
        Film film = new Film();
        film.setName("Spider Man");
        film.setDescription(".");
        film.setReleaseDate(LocalDate.of(2006, 6, 23));
        film.setDuration(Duration.ofMinutes(165));
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        film.addGenre(genre);

        return film;
    }
}
