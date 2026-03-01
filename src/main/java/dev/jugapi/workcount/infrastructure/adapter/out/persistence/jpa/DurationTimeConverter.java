package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalTime;

@Converter(autoApply = true)
public class DurationTimeConverter implements AttributeConverter<Duration, Time> {
    @Override
    public Time convertToDatabaseColumn(Duration duration) {
        if (duration == null)  return null;
        return Time.valueOf(LocalTime.ofNanoOfDay(duration.toNanos()));
    }

    @Override
    public Duration convertToEntityAttribute(Time time) {
        if (time == null)  return null;
        return Duration.ofNanos(time.toLocalTime().toNanoOfDay());
    }
}
