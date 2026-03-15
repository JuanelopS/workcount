package dev.jugapi.workcount.application.service;

import dev.jugapi.workcount.application.port.in.*;
import dev.jugapi.workcount.domain.exception.AlreadyRegisteredDayException;
import dev.jugapi.workcount.domain.exception.InexistentRegisteredDayException;
import dev.jugapi.workcount.domain.exception.PolicyNotFoundException;
import dev.jugapi.workcount.domain.exception.TemplateNotFoundException;
import dev.jugapi.workcount.domain.model.*;
import dev.jugapi.workcount.application.port.out.DailyPolicyRepository;
import dev.jugapi.workcount.application.port.out.WorkMonthTemplateRepository;
import dev.jugapi.workcount.application.port.out.WorkRegistrationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.Optional;

@Service
public class WorkRegistrationService implements RegisterWorkDayUseCase, DeleteWorkDayUseCase,
        FindByMonthUseCase, CalculateMonthlyBalanceUseCase, ModifyWorkDayUseCase, ClockInUseCase {

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

    public WorkDay registerWorkDay(WorkDay wd) {
        if (workRegistrationRepository.existsByWorkingDay(wd.getWorkingDay())) {
            throw new AlreadyRegisteredDayException(wd.getWorkingDay());
        }

        DayOfWeek day = wd.getWorkingDay().getDayOfWeek();

        DailyPolicy policy = dailyPolicyRepository.getPolicyFor(day)
                .orElseThrow(() -> new PolicyNotFoundException(day));
        wd = wd.validateHours(policy);
        return workRegistrationRepository.save(wd);
    }

    @Transactional
    public void deleteByWorkingDay(LocalDate wd) {
        if (workRegistrationRepository.existsByWorkingDay(wd)) {
            workRegistrationRepository.deleteByWorkingDay(wd);
        } else {
            throw new InexistentRegisteredDayException(wd);
        }
    }

    public List<WorkDay> findByMonth(YearMonth month) {
        return workRegistrationRepository.findByMonth(month);
    }

    public Duration calculateMonthlyBalance(YearMonth month) {
        List<WorkDay> registrations = workRegistrationRepository.findByMonth(month);
        WorkMonthTemplate template = workMonthTemplateRepository
                .getWorkMonthTemplate(month)
                .orElseThrow(() -> new TemplateNotFoundException(month));
        Duration target = template.monthlyTargetHours(this.weeklyTarget);
        return new WorkMonth(month, registrations, target).calculateBalance();
    }

    @Override
    public WorkDay modifyWorkDay(WorkDay wd) {
        if(!workRegistrationRepository.existsByWorkingDay(wd.getWorkingDay())) {
            throw new InexistentRegisteredDayException(wd.getWorkingDay());
        }

        DayOfWeek day = wd.getWorkingDay().getDayOfWeek();
        DailyPolicy policy = dailyPolicyRepository.getPolicyFor(day)
                .orElseThrow(() -> new PolicyNotFoundException(day));

        wd = wd.validateHours(policy);

        return workRegistrationRepository.save(wd);
    }

    @Override
    public WorkDay clockIn(ClockingType type) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        Optional<WorkDay> optWorkDay = workRegistrationRepository.findByWorkingDay(today);

        WorkDay workDay = optWorkDay.orElseGet(() -> WorkDay.create(today));

        Clocking clocking = new Clocking(now, type);
        workDay.addClocking(clocking);

        return workRegistrationRepository.save(workDay);
    }
}