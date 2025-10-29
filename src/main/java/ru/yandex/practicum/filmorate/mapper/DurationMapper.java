package ru.yandex.practicum.filmorate.mapper;

import java.time.Duration;

public class DurationMapper {
    public static Duration getDbDuration(String interval) {
        if (interval.isEmpty()) {
            return null;
        }

        String[] dividedInterval = interval.split("'")[1].split(":");

        return Duration.parse("PT" + dividedInterval[0] + "H" +
                dividedInterval[1] + "M" +
                dividedInterval[2] + "S");
    }

    public static String getInterval(Duration duration) {
        if (duration == null) {
            return null;
        }

        long hours = duration.toHours();

        long minutes = duration.toMinutes() - hours * 60;
        if (minutes < 0) {
            minutes = 0;
        }

        long seconds = duration.toSeconds() - hours * 60 * 60 - minutes * 60;
        if (seconds < 0) {
            seconds = 0;
        }

        return hours + ":" + minutes + ":" + seconds;
    }
}
