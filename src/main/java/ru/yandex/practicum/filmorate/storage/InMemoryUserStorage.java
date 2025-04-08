package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users;
    private int lastId;

    public InMemoryUserStorage(Map<Integer, User> users) {
        this.users = users;
        lastId = 0;
    }

    public Collection<User> findAll() {
        return users.values();
    }

    public User findUser(int id) {
        User user = users.get(id);

        if (user == null) {
            log.warn("Пользователь с таким id не был найден");
            throw new NotFoundException("Пользователь с таким id не был найден");
        }

        return user;
    }

    public User createUser(@RequestBody User user) {
        log.info("Начинается добавление пользователя");
        ConditionsNotMetException validateException = validateUser(user);
        if (validateException != null) {
            log.warn("Ошибка валидации при добавлении пользователя", validateException);
            throw validateException;
        }
        log.trace("Пользователь прошел валидацию");

        User newUser = new User(user, createUserId());
        log.debug("Была создана копия пользователя с добавлением id: {}", newUser);

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
            log.debug("Из-за отсутствия имени пользователя, имени было присвоено значение логина: {}", newUser);
        }

        users.put(newUser.getId(), newUser);
        log.trace("Копия была добавлен в список");

        log.info("Добавление пользователя прошло успешно");
        return newUser;
    }

    public User updateUser(@RequestBody User user) {
        log.info("Начинается обновление данных пользователя");
        if (!users.containsKey(user.getId())) {
            log.warn("Не было найдено пользователя с заданным id, при обновлении данных");
            throw new NotFoundException("Пользователь с таким id не был найден");
        }
        log.trace("Пользователь с заданным id был найден");

        User oldUser = users.get(user.getId());
        User updatedUser = new User(oldUser, oldUser.getId());
        log.debug("Была создана копия старого варианта пользователя: {}", updatedUser);

        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }
        if (user.getLogin() != null) {
            updatedUser.setLogin(user.getLogin());
        }
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getBirthday() != null) {
            updatedUser.setBirthday(user.getBirthday());
        }
        oldUser.getFriends().forEach(updatedUser::addFriend);
        log.debug("В копию были добавлены измененные данные: {}", updatedUser);

        ConditionsNotMetException validateException = validateUser(updatedUser);
        if (validateException != null) {
            log.warn("Ошибка валидации при обновлении данных пользователя", validateException);
            throw validateException;
        }
        log.trace("Измененная копия пользователя прошла валидацию");

        users.put(updatedUser.getId(), updatedUser);
        log.info("Обновление данных пользователя прошло успешно");
        return updatedUser;
    }

    private int createUserId() {
        lastId++;
        return lastId;
    }

    public ConditionsNotMetException validateUser(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            return new ConditionsNotMetException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            return new ConditionsNotMetException("Логин не может содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            return new ConditionsNotMetException("День рождения не может быть позже текущей даты");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return new ConditionsNotMetException("email не должен быть пустым");
        }
        if (!user.getEmail().contains("@")) {
            return new ConditionsNotMetException("email должен содержать символ \"@\"");
        }
        return null;
    }
}
