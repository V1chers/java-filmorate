package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryFilmStorageValidateTest {
    Film film;
    InMemoryFilmStorage inMemoryFilmStorage;

    @BeforeEach
    public void createFilmAndController() {
        film = new Film();
        film.setName("Гарри Поттер и философский камень");
        film.setDuration(159);
        film.setDescription(".");
        film.setReleaseDate(LocalDate.of(2002, 3, 21));

        inMemoryFilmStorage = new InMemoryFilmStorage(new HashMap<>());
    }

    @Test
    public void shouldPassValidation() {
        assertDoesNotThrow(() -> inMemoryFilmStorage.validateFilm(film));
    }

    @Test
    public void descriptionShouldBeTooLong() {
        film.setDescription("Жизнь десятилетнего Гарри Поттера нельзя назвать сладкой: родители умерли, едва ему" +
                " исполнился год, а от дяди и тёти, взявших сироту на воспитание, достаются лишь тычки да" +
                " подзатыльники. Но в одиннадцатый день рождения Гарри всё меняется. Странный гость, неожиданно" +
                " появившийся на пороге, приносит письмо, из которого мальчик узнаёт, что на самом деле он -" +
                " волшебник и зачислен в школу магии под названием Хогвартс. А уже через пару недель Гарри будет" +
                " мчаться в поезде Хогвартс-экспресс навстречу новой жизни, где его ждут невероятные приключения," +
                " верные друзья и самое главное — ключ к разгадке тайны смерти его родителей.");
        assertThrows(ConditionsNotMetException.class, () -> inMemoryFilmStorage.validateFilm(film));
    }

    @Test
    public void nameShouldNotBeBlankOrNull() {
        film.setName(null);
        assertThrows(ConditionsNotMetException.class, () -> inMemoryFilmStorage.validateFilm(film));

        film.setName("         ");
        assertThrows(ConditionsNotMetException.class, () -> inMemoryFilmStorage.validateFilm(film));
    }

    @Test
    public void birthdayOfCinemaShouldPass() {
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> inMemoryFilmStorage.validateFilm(film));
    }

    @Test
    public void dayBeforeBirthdayOfCinemaShouldNotPass() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ConditionsNotMetException.class, () -> inMemoryFilmStorage.validateFilm(film));
    }

    @Test
    public void zeroDurationShouldNotPass() {
        film.setDuration(0);
        assertThrows(ConditionsNotMetException.class, () -> inMemoryFilmStorage.validateFilm(film));
    }

    @Test
    public void negativeDurationShouldNotPass() {
        film.setDuration(-1);

        assertThrows(ConditionsNotMetException.class, () -> inMemoryFilmStorage.validateFilm(film));
    }
}
