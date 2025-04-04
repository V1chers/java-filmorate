package ru.yandex.practicum.filmorate.serializeranddeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Duration;

public class DurationDeserializer extends JsonDeserializer<Duration> {
    @Override
    public Duration deserialize(JsonParser arg0, DeserializationContext arg1) throws IOException {
        return Duration.ofMinutes(Integer.parseInt(arg0.getText()));
    }
}
