package dev.jugapi.workcount.domain.model;

import dev.jugapi.workcount.domain.exception.InvalidClockingSequenceException;
import org.springframework.cglib.core.Local;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class WorkDay {
    private final LocalDate workingDay;
    private final List<Clocking> registrations;
    private Duration validatedHours;

    private WorkDay(LocalDate workingDay, List<Clocking> registrations, Duration validatedHours) {

        validatedHours = validatedHours != null ? validatedHours : Duration.ZERO;
        validate(workingDay, validatedHours);

        this.workingDay = workingDay;
        this.registrations = new ArrayList<>(registrations != null ? registrations : List.of());
        this.validatedHours = validatedHours;
    }

    public static WorkDay of(LocalDate workingDay, List<Clocking> registrations,
                             Duration validatedHours) {
        return new WorkDay(workingDay, registrations, validatedHours);
    }

    public static WorkDay create(LocalDate workingDay){
        return new WorkDay(workingDay, List.of(), Duration.ZERO);
    }

    public LocalDate getWorkingDay() {
        return workingDay;
    }

    public List<Clocking> getRegistrations() {
        return registrations;
    }

    public Duration getValidatedHours() {
        return validatedHours;
    }

    public Optional<LocalTime> getStartTime() {
        return registrations.isEmpty() ? Optional.empty() :
                Optional.of(registrations.getFirst().time());
    }

    // returns the last Clocking.OUT to validate only IN - OUT duration blocks
    public Optional<LocalTime> getFinishingTime() {
        if (registrations.isEmpty() || registrations.getLast().type() != ClockingType.OUT) {
            return registrations.stream().filter(c -> c.type() == ClockingType.OUT)
                    .max(Comparator.comparing(Clocking::time))
                    .map(Clocking::time);
        }

        return Optional.of(registrations.getLast().time());
    }

    public void validate(LocalDate workingDay, Duration validatedHours) {
        if (!checkValidatedHours(validatedHours)) {
            throw new IllegalArgumentException("Validated hours can't be > 24 hours");
        }
    }

    private boolean checkValidatedHours(Duration validatedHours) {
        return validatedHours.toHours() < 24;
    }

    // refactorize to event-based-clocking

    public void addClocking(Clocking clocking) {
        if (registrations.size() % 2 != 0 && clocking.type() == ClockingType.IN) {
            throw new InvalidClockingSequenceException(clocking);
        }
        if (registrations.size() % 2 == 0 && clocking.type() == ClockingType.OUT) {
            throw new InvalidClockingSequenceException(clocking);
        }
        if (!registrations.isEmpty() && clocking.time().isBefore(registrations.getLast().time())) {
            throw new InvalidClockingSequenceException(clocking);
        }
        if(registrations.isEmpty() && clocking.type() == ClockingType.OUT) {
            throw new InvalidClockingSequenceException(clocking);
        }

        registrations.add(clocking);
        registrations.sort(Comparator.comparing(Clocking::time));
    }

    public Duration calculateTotalHours() {
        if (this.registrations.isEmpty())
            return Duration.ZERO;

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

    public WorkDay validateHours(DailyPolicy policy) {
        Optional<LocalTime> optRealStart = this.getStartTime();
        Optional<LocalTime> optRealFinishing = this.getFinishingTime();

        if (optRealStart.isEmpty() || optRealFinishing.isEmpty()) {
            this.validatedHours = Duration.ZERO;
            return this;
        }

        LocalTime realStart = optRealStart.get();
        LocalTime realFinishing = optRealFinishing.get();

        LocalTime policyStart = policy.adjustEntry(realStart);
        LocalTime policyFinishing = policy.adjustExit(realFinishing);

        Duration cutStart = Duration.between(realStart, policyStart);
        if (cutStart.isNegative())
            cutStart = Duration.ZERO;

        Duration cutFinishing = Duration.between(policyFinishing, realFinishing);
        if (cutFinishing.isNegative())
            cutFinishing = Duration.ZERO;

        this.validatedHours = this.calculateTotalHours()
                .minus(cutStart)
                .minus(cutFinishing);

        return this;
    }
}