package ru.yandex.practicum.filmorate.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

@Component
@Slf4j
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film(resultSet.getInt("ID"), new HashSet<>(), new HashSet<>());
        film.setName(resultSet.getString("NAME"));
        film.setDescription(resultSet.getString("DESCRIPTION"));
        film.setReleaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate());
        film.setDuration(DurationMapper.getDbDuration(resultSet.getString("DURATION")));
        Mpa map = new Mpa();
        map.setId(resultSet.getInt("AGE_RATING"));
        film.setMpa(map);

        return film;
    }
}
