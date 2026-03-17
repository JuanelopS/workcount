package dev.jugapi.workcount.application.service;

import dev.jugapi.workcount.application.port.in.*;
import dev.jugapi.workcount.domain.exception.AlreadyWorkDayException;
import dev.jugapi.workcount.domain.exception.InexistentWorkDayException;
import dev.jugapi.workcount.domain.exception.PolicyNotFoundException;
import dev.jugapi.workcount.domain.exception.TemplateNotFoundException;
import dev.jugapi.workcount.domain.model.*;
import dev.jugapi.workcount.application.port.out.DailyPolicyRepository;
import dev.jugapi.workcount.application.port.out.WorkMonthTemplateRepository;
import dev.jugapi.workcount.application.port.out.WorkDayRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.Optional;

@Service
public class WorkDayService implements ClockInUseCase, CreateWorkDayUseCase, UpdateWorkDayUseCase,
        DeleteWorkDayUseCase, FindWorkDayByMonthUseCase, CalculateMonthlyBalanceUseCase,
        GetCurrentStatusUseCase  {

    private final WorkDayRepository workDayRepository;
    private final WorkMonthTemplateRepository workMonthTemplateRepository;
    private final DailyPolicyRepository dailyPolicyRepository;
    private final Duration weeklyTarget;

    public WorkDayService(WorkDayRepository workDayRepository,
                          WorkMonthTemplateRepository workMonthTemplateRepository,
                          DailyPolicyRepository dailyPolicyRepository,
                          @Value("${ss.policy.target-weekly-hours}") Duration weeklyTarget) {
        this.workDayRepository = workDayRepository;
        this.workMonthTemplateRepository = workMonthTemplateRepository;
        this.dailyPolicyRepository = dailyPolicyRepository;
        this.weeklyTarget = weeklyTarget;
    }

    @Override
    public WorkDay clockIn(ClockingType type) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        Optional<WorkDay> optWorkDay = workDayRepository.findByDate(today);

        WorkDay workDay = optWorkDay.orElseGet(() -> WorkDay.create(today));

        Clocking clocking = new Clocking(now, type);
        workDay.addClocking(clocking);

        return workDayRepository.save(workDay);
    }

    public WorkDay createWorkDay(WorkDay wd) {
        if (workDayRepository.exists(wd.getDay())) {
            throw new AlreadyWorkDayException(wd.getDay());
        }

        DayOfWeek day = wd.getDay().getDayOfWeek();

        DailyPolicy policy = dailyPolicyRepository.getPolicyFor(day)
                .orElseThrow(() -> new PolicyNotFoundException(day));
        wd = wd.validateHours(policy);
        return workDayRepository.save(wd);
    }

    @Override
    public WorkDay updateWorkDay(WorkDay wd) {
        if(!workDayRepository.exists(wd.getDay())) {
            throw new InexistentWorkDayException(wd.getDay());
        }

        DayOfWeek day = wd.getDay().getDayOfWeek();
        DailyPolicy policy = dailyPolicyRepository.getPolicyFor(day)
                .orElseThrow(() -> new PolicyNotFoundException(day));

        wd = wd.validateHours(policy);

        return workDayRepository.save(wd);
    }

    @Override
    @Transactional
    public void deleteWorkDay(LocalDate date) {
        if (workDayRepository.exists(date)) {
            workDayRepository.delete(date);
        } else {
            throw new InexistentWorkDayException(date);
        }
    }

    public List<WorkDay> findWorkDayByMonth(YearMonth month) {
        return workDayRepository.findByMonth(month);
    }

    public Duration calculateMonthlyBalance(YearMonth month) {
        List<WorkDay> registrations = workDayRepository.findByMonth(month);
        WorkMonthTemplate template = workMonthTemplateRepository
                .getWorkMonthTemplate(month)
                .orElseThrow(() -> new TemplateNotFoundException(month));
        Duration target = template.monthlyTargetHours(this.weeklyTarget);
        return new WorkMonth(month, registrations, target).calculateBalance();
    }

    @Override
    public Optional<ClockingType> getCurrentStatus() {
        LocalDate today = LocalDate.now();

        if(!workDayRepository.exists(today)){
            throw new InexistentWorkDayException(today);
        }

        return workDayRepository.findByDate(today).flatMap(WorkDay::getCurrentStatus);
    }
}