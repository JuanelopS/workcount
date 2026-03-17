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

    @Override
    public Optional<ClockingType> getCurrentStatus() {
        LocalDate today = LocalDate.now();

        if(!workDayRepository.exists(today)){
            throw new InexistentWorkDayException(today);
        }

        return workDayRepository.findByDate(today).flatMap(WorkDay::getCurrentStatus);
    }
}