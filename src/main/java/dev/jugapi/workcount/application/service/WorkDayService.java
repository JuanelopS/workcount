package dev.jugapi.workcount.application.service;

import dev.jugapi.workcount.application.port.in.workday.*;
import dev.jugapi.workcount.domain.exception.AlreadyWorkDayException;
import dev.jugapi.workcount.domain.exception.InexistentWorkDayException;
import dev.jugapi.workcount.domain.exception.PolicyNotFoundException;
import dev.jugapi.workcount.domain.model.*;
import dev.jugapi.workcount.application.port.out.DailyPolicyRepository;
import dev.jugapi.workcount.application.port.out.WorkMonthTemplateRepository;
import dev.jugapi.workcount.application.port.out.WorkDayRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.Optional;

@Service
public class WorkDayService implements CreateWorkDayUseCase, UpdateWorkDayUseCase,
        DeleteWorkDayUseCase, FindWorkDayByMonthUseCase, GetCurrentStatusUseCase {

    private final WorkDayRepository workDayRepository;
    private final DailyPolicyRepository dailyPolicyRepository;

    public WorkDayService(WorkDayRepository workDayRepository,
                          WorkMonthTemplateRepository workMonthTemplateRepository,
                          DailyPolicyRepository dailyPolicyRepository) {
        this.workDayRepository = workDayRepository;
        this.dailyPolicyRepository = dailyPolicyRepository;
    }

    public WorkDay createWorkDay(WorkDay workDay) {
        if (workDayRepository.exists(workDay.getDay())) {
            throw new AlreadyWorkDayException(workDay.getDay());
        }

        DayOfWeek day = workDay.getDay().getDayOfWeek();

        DailyPolicy policy = dailyPolicyRepository.getPolicyFor(day)
                .orElseThrow(() -> new PolicyNotFoundException(day));

        workDay = workDay.calculateHoursAccordingToPolicy(policy);
        return workDayRepository.save(workDay);
    }

    @Override
    @Transactional
    public WorkDay updateWorkDay(WorkDay workDay) {
        if(!workDayRepository.exists(workDay.getDay())) {
            throw new InexistentWorkDayException(workDay.getDay());
        }

        DayOfWeek day = workDay.getDay().getDayOfWeek();
        DailyPolicy policy = dailyPolicyRepository.getPolicyFor(day)
                .orElseThrow(() -> new PolicyNotFoundException(day));

        workDay = workDay.calculateHoursAccordingToPolicy(policy);
        return workDayRepository.save(workDay);
    }

    @Override
    @Transactional
    public void deleteWorkDay(LocalDate date) {
        if (!workDayRepository.exists(date)) {
            throw new InexistentWorkDayException(date);
        }
        workDayRepository.delete(date);
    }

    public List<WorkDay> findWorkDayByMonth(YearMonth month) {
        return workDayRepository.findByMonth(month);
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