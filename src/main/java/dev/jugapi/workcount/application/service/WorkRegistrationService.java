package dev.jugapi.workcount.application.service;

import dev.jugapi.workcount.application.port.in.CalculateMonthlyBalanceUseCase;
import dev.jugapi.workcount.application.port.in.DeleteWorkDayUseCase;
import dev.jugapi.workcount.application.port.in.FindByMonthUseCase;
import dev.jugapi.workcount.application.port.in.RegisterWorkDayUseCase;
import dev.jugapi.workcount.domain.exception.AlreadyRegisteredDayException;
import dev.jugapi.workcount.domain.exception.InexistentRegisteredDayException;
import dev.jugapi.workcount.domain.exception.PolicyNotFoundException;
import dev.jugapi.workcount.domain.model.DailyPolicy;
import dev.jugapi.workcount.domain.model.WorkMonth;
import dev.jugapi.workcount.domain.model.WorkMonthTemplate;
import dev.jugapi.workcount.domain.model.WorkRegistration;
import dev.jugapi.workcount.application.port.out.DailyPolicyRepository;
import dev.jugapi.workcount.application.port.out.WorkMonthTemplateRepository;
import dev.jugapi.workcount.application.port.out.WorkRegistrationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class WorkRegistrationService implements RegisterWorkDayUseCase, DeleteWorkDayUseCase,
        FindByMonthUseCase, CalculateMonthlyBalanceUseCase {

    private final WorkRegistrationRepository workRegistrationRepository;
    private final WorkMonthTemplateRepository workMonthTemplateRepository;
    private final DailyPolicyRepository dailyPolicyRepository;
    private final Duration weeklyTarget;

    public WorkRegistrationService(WorkRegistrationRepository workRegistrationRepository,
                                   WorkMonthTemplateRepository workMonthTemplateRepository,
                                   DailyPolicyRepository dailyPolicyRepository,
                                   @Value("${ss.policy.target-weekly-hours}") Duration weeklyTarget) {
        this.workRegistrationRepository = workRegistrationRepository;
        this.workMonthTemplateRepository = workMonthTemplateRepository;
        this.dailyPolicyRepository = dailyPolicyRepository;
        this.weeklyTarget = weeklyTarget;
    }

    public WorkRegistration registerWorkDay(WorkRegistration wr) {
        DayOfWeek day = wr.getWorkingDay().getDayOfWeek();

        if (workRegistrationRepository.existsByWorkingDay(wr.getWorkingDay())) {
            throw new AlreadyRegisteredDayException(wr.getWorkingDay());
        }

        DailyPolicy policy = dailyPolicyRepository.getPolicyFor(day)
                .orElseThrow(() -> new PolicyNotFoundException(day));
        wr = wr.validateHours(policy);
        return workRegistrationRepository.save(wr);
    }

    @Transactional
    public void deleteByWorkingDay(LocalDate workingDay) {
        if (workRegistrationRepository.existsByWorkingDay(workingDay)) {
            workRegistrationRepository.deleteByWorkingDay(workingDay);
        } else {
            throw new InexistentRegisteredDayException(workingDay);
        }
    }

    public List<WorkRegistration> findByMonth(YearMonth month) {
        return workRegistrationRepository.findByMonth(month);
    }

    public Duration calculateMonthlyBalance(YearMonth month) {
        List<WorkRegistration> registrations = workRegistrationRepository.findByMonth(month);
        WorkMonthTemplate template = workMonthTemplateRepository
                .getWorkMonthTemplate(month)
                .orElseThrow(() -> new RuntimeException("Template not found: " + month));
        Duration target = template.monthlyTargetHours(this.weeklyTarget);
        return new WorkMonth(month, registrations, target).calculateBalance();
    }
}