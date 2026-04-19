package dev.jugapi.workcount.domain.model.clocking;

import java.time.LocalTime;

public record Clocking(
        LocalTime time,
        ClockingType type) {
}
