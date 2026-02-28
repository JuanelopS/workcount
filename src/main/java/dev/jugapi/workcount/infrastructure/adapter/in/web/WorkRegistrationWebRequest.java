package dev.jugapi.workcount.infrastructure.adapter.in.web;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public record WorkRegistrationWebRequest(
        LocalDate workingDay, LocalTime startTime, LocalTime finishingTime,
        Duration breakDuration, Duration validatedHours) {
}
