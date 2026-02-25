package dev.jugapi.workcount.application.service;

import dev.jugapi.workcount.domain.model.WorkMonth;
import dev.jugapi.workcount.domain.model.WorkRegistration;
import dev.jugapi.workcount.domain.port.WorkRegistrationRepository;

import java.time.Duration;
import java.time.YearMonth;
import java.util.List;

public class WorkRegistrationService {
    private WorkRegistrationRepository repository;

    public WorkRegistrationService(WorkRegistrationRepository repository) {
        this.repository = repository;
    }

    public void registerDay(WorkRegistration registration) {
        repository.save(registration);
    }

    public List<WorkRegistration> findByMonth(YearMonth month) {
        return repository.findByMonth(month);
    }

    public Duration calculateMonthlyBalance(YearMonth month) {
        List<WorkRegistration> registrations = repository.findByMonth(month);
        WorkMonth workMonth = new WorkMonth(month, registrations, Duration.ofHours(160));
        return workMonth.calculateBalance();
    }
}