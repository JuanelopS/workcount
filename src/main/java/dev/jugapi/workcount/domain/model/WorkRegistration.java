package dev.jugapi.workcount.domain.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class WorkRegistration {
    private LocalDate workingDay;
    private LocalTime startTime;
    private LocalTime finishingTime;
    private Duration breakDuration;
    private Duration validatedHours;

    public WorkRegistration(LocalDate workingDay, LocalTime startTime, LocalTime finishingTime,
                            Duration breakDuration, Duration validatedHours) {
        this.workingDay = workingDay;
        this.startTime = startTime;
        this.finishingTime = finishingTime;
        this.breakDuration = (breakDuration != null) ? breakDuration : Duration.ZERO; // unused
        this.validatedHours = (validatedHours != null) ? validatedHours : Duration.ZERO;
    }

    public LocalDate getWorkingDay() {
        return workingDay;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getFinishingTime() {
        return finishingTime;
    }

    public Duration getBreakDuration() {
        return breakDuration;
    }

    public Duration getValidatedHours() {
        return validatedHours;
    }

    public void validateHours(WorkPolicy policy) {
        LocalTime realStart = startTime.isBefore(policy.getLimitEntryTime()) ?
                policy.getLimitEntryTime() : startTime;
        LocalTime realEnd = finishingTime.isAfter(policy.getLimitExitTime()) ?
                policy.getLimitExitTime() : finishingTime;
        this.validatedHours = Duration
                .between(realStart, realEnd)
                .minus(breakDuration);
    }
}
