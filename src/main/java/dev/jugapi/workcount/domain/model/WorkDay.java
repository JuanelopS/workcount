package dev.jugapi.workcount.domain.model;

import dev.jugapi.workcount.domain.exception.InvalidClockingSequenceException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WorkDay {
    private final LocalDate workingDay;
    private final List<Clocking> registrations;
    private final LocalTime startTime;
    private final LocalTime finishingTime;
    private final Duration breakDuration;
    private Duration validatedHours;

    private WorkDay(LocalDate workingDay, List<Clocking> registrations, LocalTime startTime,
                    LocalTime finishingTime, Duration breakDuration, Duration validatedHours) {

        breakDuration = breakDuration != null ? breakDuration : Duration.ZERO;
        validatedHours = validatedHours != null ? validatedHours : Duration.ZERO;
        validate(workingDay, startTime, finishingTime, breakDuration, validatedHours);

        this.workingDay = workingDay;
        this.registrations = new ArrayList<>(registrations != null ? registrations : List.of());
        this.startTime = startTime;
        this.finishingTime = finishingTime;
        this.breakDuration = breakDuration; // unused
        this.validatedHours = validatedHours;
    }

    public static WorkDay of(LocalDate workingDay, List<Clocking> registrations, LocalTime startTime,
                             LocalTime finishingTime, Duration breakDuration,
                             Duration validatedHours) {
        return new WorkDay(workingDay, registrations, startTime, finishingTime, breakDuration,
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

    public WorkDay validateHours(DailyPolicy policy) {
        LocalTime realStart = policy.adjustEntry(startTime);
        LocalTime realEnd = policy.adjustExit(finishingTime);
        this.validatedHours = Duration.between(realStart, realEnd).minus(breakDuration);
        return this;
    }

    // refactorize to event-based-clocking

    public void addClocking(Clocking clocking) {
        if (registrations.size() % 2 != 0 && clocking.type() == ClockingType.IN) {
            throw new InvalidClockingSequenceException(clocking);
        }
        if (registrations.size() % 2 == 0 && clocking.type() == ClockingType.OUT) {
            throw new InvalidClockingSequenceException(clocking);
        }
        if(!registrations.isEmpty() && clocking.time().isBefore(registrations.getLast().time())) {
            throw new InvalidClockingSequenceException(clocking);
        }

        registrations.add(clocking);
        registrations.sort(Comparator.comparing(Clocking::time));
    }

    public Duration calculateTotalHours() {
        if (this.registrations.isEmpty()) return Duration.ZERO;

        Duration total = Duration.ZERO;
        LocalTime start = null;

        for (Clocking clocking : this.registrations) {
            if (clocking.type() == ClockingType.IN) {
                start = clocking.time();
            } else if (clocking.type() == ClockingType.OUT && start != null) {
                total = total.plus(Duration.between(start, clocking.time()));
                start = null;
            }
        }

        return total;
    }
}
