package dev.jugapi.workcount.application.service;

import dev.jugapi.workcount.application.port.in.workday.*;
import dev.jugapi.workcount.application.port.out.DailyPolicyRepository;
import dev.jugapi.workcount.application.port.out.WorkDayRepository;
import dev.jugapi.workcount.domain.exception.AlreadyWorkDayException;
import dev.jugapi.workcount.domain.exception.InexistentWorkDayException;
import dev.jugapi.workcount.domain.exception.PolicyNotFoundException;
import dev.jugapi.workcount.domain.model.ClockingType;
import dev.jugapi.workcount.domain.model.DailyPolicy;
import dev.jugapi.workcount.domain.model.WorkDay;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class WorkDayService implements CreateWorkDayUseCase, UpdateWorkDayUseCase,
        DeleteWorkDayUseCase, FindWorkDayByDateUseCase, FindWorkDaysByDateRangeUseCase,
        FindWorkDaysByMonthUseCase, GetCurrentStatusUseCase {

    private final WorkDayRepository workDayRepository;
    private final DailyPolicyRepository dailyPolicyRepository;

    public WorkDayService(WorkDayRepository workDayRepository,
                          DailyPolicyRepository dailyPolicyRepository) {
        this.workDayRepository = workDayRepository;
        this.dailyPolicyRepository = dailyPolicyRepository;
    }

    public WorkDay createWorkDay(WorkDay workDay) {
        if (workDayRepository.exists(workDay.getDate())) {
            throw new AlreadyWorkDayException(workDay.getDate());
        }

        workDay = calculateNetTimeWorked(workDay);
        return workDayRepository.save(workDay);
    }

    @Override
    @Transactional
    public WorkDay updateWorkDay(WorkDay workDay) {
        if (!workDayRepository.exists(workDay.getDate())) {
            throw new InexistentWorkDayException(workDay.getDate());
        }

        workDay = calculateNetTimeWorked(workDay);
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

    public List<WorkDay> findWorkDaysByMonth(YearMonth month) {
        return workDayRepository.findByMonth(month);
    }

    @Override
    public WorkDay findWorkDayByDate(LocalDate date) {
        return workDayRepository.findByDate(date)
                .orElseThrow(() -> new InexistentWorkDayException(date));
    }

    @Override
    public List<WorkDay> findWorkDaysByDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("The start date must be before or equal to the end date");
        }
        return workDayRepository.findByDateBetween(from, to);
    }

    @Override
    public Optional<ClockingType> getCurrentStatus() {
        LocalDate today = LocalDate.now();

        if (!workDayRepository.exists(today)) {
            throw new InexistentWorkDayException(today);
        }

        return workDayRepository.findByDate(today).flatMap(WorkDay::getCurrentStatus);
    }

    private WorkDay calculateNetTimeWorked(WorkDay workDay) {
        DayOfWeek day = workDay.getDate().getDayOfWeek();
        Optional<DailyPolicy> policy = dailyPolicyRepository.getPolicyFor(day);

        if (policy.isEmpty()) {
            throw new PolicyNotFoundException(day, workDay.getDate());
        }

        return workDay.calculateNetTimeWorked(policy.get());
    }


}