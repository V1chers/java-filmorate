package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendRowMapper implements RowMapper<Friend> {
    @Override
    public Friend mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new Friend(
                resultSet.getInt("FRIEND_ID"),
                resultSet.getBoolean("IS_CONFIRMED")
        );
    }
}
