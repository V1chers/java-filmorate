package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class})
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    public void shouldCreateAndFindUser() {
        User user = createUser();
        User createdUser = userStorage.createUser(user);
        User expectedUser = new User(user, createdUser.getId());

        assertEquals(expectedUser, userStorage.findUser(createdUser.getId()));
    }

    @Test
    public void shouldCheckFriendship() {
        User user = createUser();
        user.addFriend(2);
        int createdUserId = userStorage.createUser(user).getId();
        user = userStorage.findUser(createdUserId);

        Optional<Friend> friend = user.getFriends().stream()
                .filter(frnd -> frnd.getId() == 2)
                .findFirst();

        assertFalse(friend.get().getIsConfirmed());

        User user2 = userStorage.findUser(2);
        user2.addFriend(createdUserId);
        userStorage.updateUser(user2);
        user = userStorage.findUser(createdUserId);
        user2 = userStorage.findUser(2);

        friend = user.getFriends().stream()
                .filter(frnd -> frnd.getId() == 2)
                .findFirst();
        Optional<Friend> friend2 = user2.getFriends().stream()
                .filter(frnd -> frnd.getId() == createdUserId)
                .findFirst();

        assertTrue(friend.get().getIsConfirmed());
        assertTrue(friend2.get().getIsConfirmed());
    }

    @Test
    public void shouldNotUpdateUnknownUser() {
        User user = createUser();

        assertThrows(NotFoundException.class, () -> userStorage.updateUser(user));
    }

    @Test
    public void shouldNotAddUnknownFriend() {
        User user = userStorage.findUser(1);
        user.addFriend(365);

        assertThrows(NotFoundException.class, () -> userStorage.updateUser(user));
    }

    @Test
    public void shouldDeleteFriendInBothUsers() {
        User user = userStorage.findUser(1);
        User user2 = userStorage.findUser(2);

        assertTrue(user.getFriends().contains(new Friend(2, true)));
        assertTrue(user2.getFriends().contains(new Friend(1, true)));

        user.deleteFriend(new Friend(2, true));
        userStorage.updateUser(user);

        user = userStorage.findUser(1);
        user2 = userStorage.findUser(2);

        assertFalse(user.getFriends().contains(new Friend(2, true)));
        assertFalse(user2.getFriends().contains(new Friend(1, true)));
    }

    private User createUser() {
        User user = new User();
        user.setLogin("123");
        user.setName("123");
        user.setBirthday(LocalDate.of(1999, 1, 1));
        user.setEmail("123@mail.ru");
        user.addFriend(1);

        return user;
    }
}
