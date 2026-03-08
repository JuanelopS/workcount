package dev.jugapi.workcount.infrastructure.adapter.in.web;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public record WorkRegistrationWebResponse(
        LocalDate workingDay, LocalTime startTime, LocalTime finishingTime,
        Duration breakDuration,  Double validatedHours) {
}
