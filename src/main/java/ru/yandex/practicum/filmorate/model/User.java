package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private final int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public User() {
        id = -1;
    }

    public User(User user, int id) {
        this.id = id;
        this.email = user.getEmail();
        this.login = user.getLogin();
        this.name = user.getName();
        this.birthday = user.getBirthday();
    }
}
