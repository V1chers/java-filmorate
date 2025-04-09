package ru.yandex.practicum.filmorate.storage;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryUserStorageValidateTest {
    User user;
    InMemoryUserStorage inMemoryUserStorage;
    Validator validator;
    Set<ConstraintViolation<User>> violations;

    @BeforeEach
    public void createUserAndInMemoryUserStorage() {
        user = new User();
        user.setLogin("RandomUser1000-7");
        user.setName("Борис");
        user.setEmail("RandomUser1000-7@gmail.com");
        user.setBirthday(LocalDate.of(2018, 2, 12));

        inMemoryUserStorage = new InMemoryUserStorage(new HashMap<>());

        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void shouldPassValidation() {
        violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void loginShouldNotBeBlankOrNull() {
        user.setLogin(null);
        violations = validator.validate(user);

        assertFalse(violations.isEmpty());

        user.setLogin("      ");
        violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    public void todayBirthdayShouldNotPass() {
        user.setBirthday(LocalDate.now());
        violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    public void tomorrowBirthdayShouldNotPass() {
        user.setBirthday(LocalDate.now().plusDays(1));
        violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    public void emailShouldNotBeBlankOrNull() {
        user.setEmail(null);
        violations = validator.validate(user);

        assertFalse(violations.isEmpty());

        user.setEmail("      ");
        violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    public void emailShouldContainAtSign() {
        user.setEmail("RandomUser1000-7gmail.com");
        violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    public void loginShouldNotContainSpace() {
        user.setLogin("Random User 1000-7");
        violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }
}
