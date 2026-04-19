package dev.jugapi.workcount.infrastructure.adapter.in.web.clocking;

import dev.jugapi.workcount.domain.model.ClockingType;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateClockingRequest(
        LocalDate date,
        LocalTime originalTime,
        LocalTime newTime,
        ClockingType type) {
}
