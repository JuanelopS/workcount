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
public class WorkDayService implements ClockInUseCase, CreateWorkDayUseCase, DeleteWorkDayUseCase,
        FindWorkDayByMonthUseCase, CalculateMonthlyBalanceUseCase, UpdateWorkDayUseCase {

    private final WorkRegistrationRepository workRegistrationRepository;
    private final WorkMonthTemplateRepository workMonthTemplateRepository;
    private final DailyPolicyRepository dailyPolicyRepository;
    private final Duration weeklyTarget;

    public WorkDayService(WorkRegistrationRepository workRegistrationRepository,
                          WorkMonthTemplateRepository workMonthTemplateRepository,
                          DailyPolicyRepository dailyPolicyRepository,
                          @Value("${ss.policy.target-weekly-hours}") Duration weeklyTarget) {
        this.workRegistrationRepository = workRegistrationRepository;
        this.workMonthTemplateRepository = workMonthTemplateRepository;
        this.dailyPolicyRepository = dailyPolicyRepository;
        this.weeklyTarget = weeklyTarget;
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

    public WorkDay createWorkDay(WorkDay wd) {
        if (workRegistrationRepository.exists(wd.getWorkingDay())) {
            throw new AlreadyRegisteredDayException(wd.getWorkingDay());
        }

        DayOfWeek day = wd.getWorkingDay().getDayOfWeek();

        DailyPolicy policy = dailyPolicyRepository.getPolicyFor(day)
                .orElseThrow(() -> new PolicyNotFoundException(day));
        wd = wd.validateHours(policy);
        return workRegistrationRepository.save(wd);
    }

    @Override
    public WorkDay updateWorkDay(WorkDay wd) {
        if(!workRegistrationRepository.exists(wd.getWorkingDay())) {
            throw new InexistentRegisteredDayException(wd.getWorkingDay());
        }

        DayOfWeek day = wd.getWorkingDay().getDayOfWeek();
        DailyPolicy policy = dailyPolicyRepository.getPolicyFor(day)
                .orElseThrow(() -> new PolicyNotFoundException(day));

        wd = wd.validateHours(policy);

        return workRegistrationRepository.save(wd);
    }

    @Override
    @Transactional
    public void deleteWorkDay(LocalDate wd) {
        if (workRegistrationRepository.exists(wd)) {
            workRegistrationRepository.delete(wd);
        } else {
            throw new InexistentRegisteredDayException(wd);
        }
    }

    public List<WorkDay> findWorkDayByMonth(YearMonth month) {
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
}