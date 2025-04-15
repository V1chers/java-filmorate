package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FriendRowMapper;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper userMapper;
    private final FriendRowMapper friendMapper;

    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper userMapper, FriendRowMapper friendMapper) {
        this.jdbc = jdbc;
        this.userMapper = userMapper;
        this.friendMapper = friendMapper;
    }

    public User createUser(User user) {
        checkFriendList(user);

        Date birthday = user.getBirthday() == null ? null : Date.valueOf(user.getBirthday());

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String insertUser = "INSERT INTO users(EMAIL, LOGIN, NAME, BIRTHDAY)" +
                "VALUES(?, ?, ?, ?)";
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, birthday);
            return ps;
        }, keyHolder);
        User userWithId = new User(user, keyHolder.getKeyAs(Integer.class));

        updateFriends(userWithId);

        return userWithId;
    }

    public User findUser(int id) {
        String findUser = "SELECT * " +
                "FROM users " +
                "WHERE id = ? ";
        String findUserFriends = "SELECT * " +
                "FROM friends " +
                "WHERE user_id = ? ";

        try {
            User user = jdbc.queryForObject(findUser, userMapper, id);
            jdbc.query(findUserFriends, friendMapper, id).forEach(user::addFriend);
            return user;
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Пользователь с таким id не был найден");
        }
    }

    public Collection<User> findAll() {
        String findUser = "SELECT *" +
                "FROM users";
        return jdbc.query(findUser, userMapper);
    }

    public User updateUser(User user) {
        checkFriendList(user);

        String updateUser = "UPDATE users SET login = ?, email = ?, name = ?, birthday = ? WHERE id = ?";

        int rowsUpdated = jdbc.update(updateUser, user.getLogin(), user.getEmail(), user.getName(),
                user.getBirthday(), user.getId());

        if (rowsUpdated == 0) {
            throw new NotFoundException("Такого пользователя не существует");
        }

        updateFriends(user);

        return user;
    }

    private void updateFriends(User user) {
        Set<Integer> newFriendList = user.getFriends().stream().map(Friend::getId).collect(Collectors.toSet());
        Set<Integer> oldFriendList = getDbFriends(user).stream().map(Friend::getId).collect(Collectors.toSet());

        Set<Integer> listToDelete = new HashSet<>();
        Set<Integer> listToAdd = new HashSet<>();

        newFriendList.forEach(friendId -> {
            if (!oldFriendList.contains(friendId)) {
                listToAdd.add(friendId);
            }
        });
        oldFriendList.forEach(friendId -> {
            if (!newFriendList.contains(friendId)) {
                listToDelete.add(friendId);
            }
        });

        listToAdd.forEach(friendId -> addFriend(user.getId(), friendId));
        listToDelete.forEach(friendId -> deleteFriend(user.getId(), friendId));
    }

    private void addFriend(int userId, int friendId) {
        String getFriendsRequest = "SELECT is_confirmed " +
                "FROM friends " +
                "WHERE user_id = ? " +
                "AND friend_Id = ? ";

        String updateFriendsRequest = "UPDATE friends " +
                "SET is_confirmed = true " +
                "WHERE user_id = ? " +
                "AND friend_Id = ? ";

        String insertFriend = "INSERT INTO friends(user_id, friend_Id, is_confirmed) " +
                "VALUES(?, ?, ?) ";

        try {
            Boolean isFriendRequestExistAndConfirmed = jdbc.queryForObject(getFriendsRequest, Boolean.class, friendId,
                    userId);

            if (isFriendRequestExistAndConfirmed) {
                return;
            }

            jdbc.update(updateFriendsRequest, friendId, userId);
            jdbc.update(insertFriend, userId, friendId, true);

        } catch (EmptyResultDataAccessException ignored) {
            jdbc.update(insertFriend, userId, friendId, false);
        }
    }

    private void deleteFriend(int userId, int friendId) {
        String deleteFriend = "DELETE FROM friends " +
                "WHERE user_id = ? " +
                "AND friend_id = ? ";
        jdbc.update(deleteFriend, userId, friendId);
        jdbc.update(deleteFriend, friendId, userId);
    }

    private Set<Friend> getDbFriends(User user) {
        String getFriends = "SELECT * " +
                "FROM friends " +
                "WHERE user_id = ?";

        return new HashSet<>(jdbc.query(getFriends, friendMapper, user.getId()));
    }

    private void checkFriendList(User user) {
        try {
            user.getFriends().forEach(friend -> findUser(friend.getId()));
        } catch (NotFoundException e) {
            throw new NotFoundException("Друг с таким id не был найден");
        }
    }
}
