package dev.jugapi.workcount.domain.model;

import java.time.Duration;
import java.time.YearMonth;
import java.util.List;

public record WorkMonth(
        YearMonth month,
        List<WorkRegistration> registrations,
        Duration targetHours) {

    public Duration calculateTotalHoursWorked() {
        return registrations.stream()
                .map(WorkRegistration::getValidatedHours)
                .reduce(Duration::plus)
                .orElse(Duration.ZERO);
    }

    public Duration calculateBalance() {
        return this.calculateTotalHoursWorked().minus(targetHours);
    }
}
