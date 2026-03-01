package dev.jugapi.workcount.application.service;

import dev.jugapi.workcount.domain.exception.PolicyNotFoundException;
import dev.jugapi.workcount.domain.model.DailyPolicy;
import dev.jugapi.workcount.domain.model.WorkMonth;
import dev.jugapi.workcount.domain.model.WorkMonthTemplate;
import dev.jugapi.workcount.domain.model.WorkRegistration;
import dev.jugapi.workcount.domain.port.DailyPolicyRepository;
import dev.jugapi.workcount.domain.port.WorkMonthTemplateRepository;
import dev.jugapi.workcount.domain.port.WorkRegistrationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class WorkRegistrationService {
    private final WorkRegistrationRepository wrRepo;
    private final WorkMonthTemplateRepository wmtRepo;
    private final DailyPolicyRepository dpRepo;
    private final Duration weeklyTarget;

    public WorkRegistrationService(WorkRegistrationRepository wrRpo,
                                   WorkMonthTemplateRepository wmtRepo,
                                   DailyPolicyRepository dpRepo,
                                   @Value("${ss.policy.target-weekly-hours}") Duration weeklyHours) {
        this.wrRepo = wrRpo;
        this.wmtRepo = wmtRepo;
        this.dpRepo = dpRepo;
        this.weeklyTarget = weeklyHours;
    }

    public void registerDay(WorkRegistration registration) {
        DayOfWeek day = registration.getWorkingDay().getDayOfWeek();
        DailyPolicy policy = dpRepo.getPolicyFor(day)
                .orElseThrow(() -> new PolicyNotFoundException(day));
        registration = registration.validateHours(policy);
        wrRepo.save(registration);
    }

    public List<WorkRegistration> findByMonth(YearMonth month) {
        return wrRepo.findByMonth(month);
    }

    public Duration calculateMonthlyBalance(YearMonth month) {
        List<WorkRegistration> registrations = wrRepo.findByMonth(month);
        WorkMonthTemplate template = wmtRepo
                .getWorkMonthTemplate(month)
                .orElseThrow(() -> new RuntimeException("Template not found: " + month));
        Duration target = template.monthlyTargetHours(this.weeklyTarget);
        return new WorkMonth(month, registrations, target).calculateBalance();
    }
}