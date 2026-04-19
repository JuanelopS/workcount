package dev.jugapi.workcount.domain.model.workmonth;

import dev.jugapi.workcount.domain.model.workday.WorkDay;

import java.time.Duration;
import java.time.YearMonth;
import java.util.List;

public record WorkMonth(
        YearMonth month,
        List<WorkDay> registrations,
        Duration targetHours) {

    public WorkMonth {
        registrations = (registrations != null) ? List.copyOf(registrations) : List.of();

        boolean checkList = registrations.stream()
                .anyMatch(wr -> !YearMonth.from(wr.getDate()).equals(month));
        if (checkList) {
            throw new IllegalArgumentException("All registration must belong to the month " + month);
        }

        targetHours = (targetHours == null || targetHours.isNegative()) ? Duration.ZERO
                : targetHours;
    }

    public Duration calculateTotalHoursWorked() {
        return registrations.stream()
                .map(WorkDay::getNetTimeWorked)
                .reduce(Duration::plus)
                .orElse(Duration.ZERO);
    }

    public Duration calculateBalance() {
        return this.calculateTotalHoursWorked().minus(targetHours);
    }
}
