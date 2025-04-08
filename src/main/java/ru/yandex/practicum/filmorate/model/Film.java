package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.serializeranddeserializer.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film {
    private final int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;
    private final Set<Integer> likes;

    public Film() {
        id = -1;
        likes = new HashSet<>();
    }

    public Film(Film film, int id) {
        this.id = id;
        this.name = film.getName();
        this.description = film.getDescription();
        this.releaseDate = film.getReleaseDate();
        this.duration = film.getDuration();
        this.likes = new HashSet<>(film.getLikes());
    }

    @Autowired
    public void setDuration(int duration) {
        this.duration = Duration.ofMinutes(duration);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public boolean addLike(int userId) {
        return likes.add(userId);
    }

    public boolean deleteLike(int userId) {
        return likes.remove(userId);
    }
}
