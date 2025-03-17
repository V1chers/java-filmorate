package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.yandex.practicum.filmorate.SerializersAndDeserializers.DurationDeserializer;
import ru.yandex.practicum.filmorate.SerializersAndDeserializers.DurationSerializer;

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
    @JsonDeserialize(using = DurationDeserializer.class)
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
}
