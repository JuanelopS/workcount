package dev.jugapi.workcount.domain.model;

import java.time.LocalTime;

enum ClockingType {
    IN, OUT
}

public record Clocking(
        LocalTime time,
        ClockingType type) {
}
