package dev.jugapi.workcount.domain.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class WorkRegistration {
    private final LocalDate workingDay;
    private final LocalTime startTime;
    private final LocalTime finishingTime;
    private final Duration breakDuration;
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

    public WorkRegistration validateHours(DailyPolicy policy) {
        LocalTime realStart = startTime.isBefore(policy.limitEntryTime()) ?
                policy.limitEntryTime() : startTime;
        LocalTime realEnd = finishingTime.isAfter(policy.limitExitTime()) ?
                policy.limitExitTime() : finishingTime;
        this.validatedHours = Duration
                .between(realStart, realEnd)
                .minus(breakDuration);
        return this;
    }
}
