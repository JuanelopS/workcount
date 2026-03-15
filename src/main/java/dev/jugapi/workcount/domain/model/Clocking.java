package dev.jugapi.workcount.domain.model;

import java.time.LocalTime;

public record Clocking(
        LocalTime time,
        ClockingType type) {
}
