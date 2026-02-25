package dev.jugapi.workcount.domain.model;

import java.time.Duration;
import java.time.YearMonth;
import java.util.List;

public class WorkMonth {
    private YearMonth month;
    private List<WorkRegistration> registrations;
    private Duration targetHours;

    public WorkMonth(YearMonth month, List<WorkRegistration> registrations, Duration targetHours) {
        this.month = month;
        this.registrations = registrations;
        this.targetHours = targetHours;
    }

    public YearMonth getMonth() {
        return month;
    }

    public List<WorkRegistration> getRegistrations() {
        return registrations;
    }

    public Duration getTargetHours() {
        return targetHours;
    }

    public Duration calculateTotalHoursWorked() {
        return registrations.stream()
                .map(WorkRegistration::getValidatedHours)
                .reduce(Duration::plus)
                .orElse(Duration.ZERO);
    }

    public Duration calculateBalance() {
        return targetHours.minus(this.calculateTotalHoursWorked());
    }
}
