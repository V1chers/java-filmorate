package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.serializeranddeserializer.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;

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

    public Film() {
        id = -1;
    }

    public Film(Film film, int id) {
        this.id = id;
        this.name = film.getName();
        this.description = film.getDescription();
        this.releaseDate = film.getReleaseDate();
        this.duration = film.getDuration();
    }

    @Autowired
    public void setDuration(int duration) {
        this.duration = Duration.ofMinutes(duration);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
