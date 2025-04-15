package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class User {
    private final int id;
    private final Set<Friend> friends;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Pattern(regexp = "\\S+")
    private String login;
    private String name;
    @Past
    private LocalDate birthday;

    public User() {
        id = -1;
        friends = new HashSet<>();
    }

    public User(User user, int id) {
        this.id = id;
        this.email = user.getEmail();
        this.login = user.getLogin();
        this.name = user.getName();
        this.birthday = user.getBirthday();
        this.friends = new HashSet<>(user.getFriends());
    }

    public boolean addFriend(int friendId) {
        return friends.add(new Friend(friendId, false));
    }

    public boolean addFriend(Friend friend) {
        return friends.add(friend);
    }

    public boolean deleteFriend(Friend friend) {
        return friends.remove(friend);
    }

    public boolean deleteFriend(int friendId) {
        Optional<Friend> friend = this.getFriends().stream()
                .filter(frnd -> frnd.getId() == friendId)
                .findFirst();
        if (friend.isEmpty()) {
            return false;
        }
        return friends.remove(friend.get());
    }
}
