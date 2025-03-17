package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerValidateTest {
    User user;
    UserController userController;
    ConditionsNotMetException validateException;

    @BeforeEach
    public void createUserAndUserController() {
        user = new User();
        user.setLogin("RandomUser1000-7");
        user.setName("Борис");
        user.setEmail("RandomUser1000-7@gmail.com");
        user.setBirthday(LocalDate.of(2018, 2, 12));

        userController = new UserController();
    }

    @Test
    public void shouldPassValidation() {
        validateException = userController.validateUser(user);

        assertNull(validateException);
    }

    @Test
    public void loginShouldNotBeBlankOrNull() {
        user.setLogin(null);
        validateException = userController.validateUser(user);

        assertEquals("Логин не может быть пустым", validateException.getMessage());

        user.setLogin("      ");
        validateException = userController.validateUser(user);

        assertEquals("Логин не может быть пустым", validateException.getMessage());
    }

    @Test
    public void todayBirthdayShouldPass() {
        user.setBirthday(LocalDate.now());
        validateException = userController.validateUser(user);

        assertNull(validateException);
    }

    @Test
    public void tomorrowBirthdayShouldNotPass() {
        user.setBirthday(LocalDate.now().plusDays(1));
        validateException = userController.validateUser(user);

        assertEquals("День рождения не может быть позже текущей даты", validateException.getMessage());
    }

    @Test
    public void emailShouldNotBeBlankOrNull() {
        user.setEmail(null);
        validateException = userController.validateUser(user);

        assertEquals("email не должен быть пустым", validateException.getMessage());

        user.setEmail("      ");
        validateException = userController.validateUser(user);

        assertEquals("email не должен быть пустым", validateException.getMessage());
    }

    @Test
    public void emailShouldContainAtSign() {
        user.setEmail("RandomUser1000-7gmail.com");
        validateException = userController.validateUser(user);

        assertEquals("email должен содержать символ \"@\"", validateException.getMessage());
    }
}
