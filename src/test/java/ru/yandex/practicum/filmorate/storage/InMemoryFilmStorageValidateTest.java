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
    ConditionsNotMetException validateException;

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
        validateException = inMemoryFilmStorage.validateFilm(film);

        assertNull(validateException);
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
        validateException = inMemoryFilmStorage.validateFilm(film);

        assertEquals("Описание не должно содержать больше 200 символов", validateException.getMessage());
    }

    @Test
    public void nameShouldNotBeBlankOrNull() {
        film.setName(null);
        validateException = inMemoryFilmStorage.validateFilm(film);

        assertEquals("Название фильма не должно быть пустым", validateException.getMessage());

        film.setName("         ");
        validateException = inMemoryFilmStorage.validateFilm(film);

        assertEquals("Название фильма не должно быть пустым", validateException.getMessage());
    }

    @Test
    public void birthdayOfCinemaShouldPass() {
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        validateException = inMemoryFilmStorage.validateFilm(film);

        assertNull(validateException);
    }

    @Test
    public void dayBeforeBirthdayOfCinemaShouldNotPass() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        validateException = inMemoryFilmStorage.validateFilm(film);

        assertEquals("Дата релиза не может быть раньше дня рождения кино", validateException.getMessage());
    }

    @Test
    public void zeroDurationShouldNotPass() {
        film.setDuration(0);
        validateException = inMemoryFilmStorage.validateFilm(film);

        assertEquals("Продолжительность фильма не может быть отрицательной или равна нулю",
                validateException.getMessage());
    }

    @Test
    public void negativeDurationShouldNotPass() {
        film.setDuration(-1);
        validateException = inMemoryFilmStorage.validateFilm(film);

        assertEquals("Продолжительность фильма не может быть отрицательной или равна нулю",
                validateException.getMessage());
    }
}
