package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(int userId, int friendId) {
        log.info("Начинается процесс добавления друга");
        if (userId == friendId) {
            log.warn("Пользователь не может добавить сам себя в друзья");
            throw new ConditionsNotMetException("Пользователь не может добавить сам себя в друзья");
        }

        User user = userStorage.findUser(userId);
        User friend = userStorage.findUser(friendId);
        log.trace("По заданным id были найдены пользователи");

        if (!user.addFriend(friendId)) {
            log.warn("Друг уже добавлен в друзья у пользователя");
            throw new ConditionsNotMetException("Друг уже добавлен в друзья у пользователя");
        }
        log.debug("Пользователю был добавлен новый друг: {}", user);
        if (!friend.addFriend(userId)) {
            log.warn("Пользователь уже добавлен в друзья у друга");
            throw new ConditionsNotMetException("Пользователь уже добавлен в друзья у друга");
        }
        log.debug("Пользователь был добавлен другу в друзья: {}", friend);

        log.info("Процесс добавления друга прошел успешно");
        return user;
    }

    public User deleteFriend(int userId, int friendId) {
        log.info("Начинается процесс удаления друга");
        User user = userStorage.findUser(userId);
        User friend = userStorage.findUser(friendId);
        log.trace("По заданным id были найдены пользователи");

        user.deleteFriend(friendId);
        log.debug("У пользователя был удален друг: {}", user);
        friend.deleteFriend(userId);
        log.debug("Пользователь был удален у друга из друзей: {}", friend);

        log.info("Процесс удаления друга прошел успешно");
        return user;
    }

    public Collection<User> findAllFriends(int userId) {
        log.info("Проходит процесс поиска друзей у пользователя");
        User user = userStorage.findUser(userId);

        return user.getFriends().stream().map(userStorage::findUser).collect(Collectors.toSet());
    }

    public Collection<User> findCommonFriends(int userId, int friendId) {
        log.info("Начинается процесс поиска общих друзей у пользователей");
        User user = userStorage.findUser(userId);
        User friend = userStorage.findUser(friendId);
        log.trace("По заданным id были найдены пользователи");
        Set<Integer> userFriendList = new HashSet<>(user.getFriends());
        log.trace("Был получен список друзей пользователя");
        Set<Integer> commonFriendList = new HashSet<>();
        log.trace("Был создан пустой список общих друзей");

        for (int id : friend.getFriends()) {
            if (!userFriendList.add(id)) {
                commonFriendList.add(id);
            }
        }
        log.debug("Был отобран список список общих друзей пользователей: {}", commonFriendList);

        log.info("Производится преобразование коллекции данных и завершение работы метода");
        return commonFriendList.stream().map(userStorage::findUser).collect(Collectors.toSet());
    }
}
