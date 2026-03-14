package dev.jugapi.workcount.domain.model;

import org.hibernate.jdbc.Work;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class WorkRegistration {
    private final LocalDate workingDay;
    private final LocalTime startTime;
    private final LocalTime finishingTime;
    private final Duration breakDuration;
    private Duration validatedHours;

    private WorkRegistration(LocalDate workingDay, LocalTime startTime, LocalTime finishingTime,
                             Duration breakDuration, Duration validatedHours) {
        breakDuration = breakDuration != null ? breakDuration : Duration.ZERO;
        validatedHours = validatedHours != null ? validatedHours : Duration.ZERO;
        validate(workingDay, startTime, finishingTime, breakDuration, validatedHours);

        this.workingDay = workingDay;
        this.startTime = startTime;
        this.finishingTime = finishingTime;
        this.breakDuration = breakDuration; // unused
        this.validatedHours = validatedHours;
    }

    public static WorkRegistration of(LocalDate workingDay, LocalTime startTime,
                                      LocalTime finishingTime, Duration breakDuration,
                                      Duration validatedHours) {
        return new WorkRegistration(workingDay, startTime, finishingTime, breakDuration,
                validatedHours);
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

    public void validate(LocalDate workingDay, LocalTime startTime, LocalTime finishingTime,
                         Duration breakDuration, Duration validatedHours) {
        if (!checkTimeRange(startTime, finishingTime)) {
            throw new IllegalArgumentException("StartTime must be before FinishingTime");
        }
        if (!checkBreakDuration(startTime, finishingTime, breakDuration)) {
            throw new IllegalArgumentException("Break duration can't be > working hours");
        }
        if (!checkValidatedHours(validatedHours)) {
            throw new IllegalArgumentException("Validated hours can't be > 24 hours");
        }
    }

    private boolean checkTimeRange(LocalTime startTime, LocalTime finishingTime) {
        return startTime.isBefore(finishingTime);
    }

    private boolean checkBreakDuration(LocalTime startTime, LocalTime finishingTime,
                                       Duration breakDuration) {
        return Duration.between(startTime, finishingTime).toHours() > breakDuration.toHours();
    }

    private boolean checkValidatedHours(Duration validatedHours) {
        return validatedHours.toHours() < 24;
    }

    public WorkRegistration validateHours(DailyPolicy policy) {
        LocalTime realStart = policy.adjustEntry(startTime);
        LocalTime realEnd = policy.adjustExit(finishingTime);
        this.validatedHours = Duration.between(realStart, realEnd).minus(breakDuration);
        return this;
    }
}
