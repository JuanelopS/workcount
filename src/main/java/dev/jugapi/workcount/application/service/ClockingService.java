package dev.jugapi.workcount.application.service;

import dev.jugapi.workcount.application.port.in.clocking.ClockInUseCase;
import dev.jugapi.workcount.application.port.in.clocking.CreateClockingUseCase;
import dev.jugapi.workcount.application.port.in.clocking.DeleteClockingUseCase;
import dev.jugapi.workcount.application.port.in.clocking.UpdateClockingUseCase;
import dev.jugapi.workcount.application.port.out.DailyPolicyRepository;
import dev.jugapi.workcount.application.port.out.WorkDayRepository;
import dev.jugapi.workcount.domain.exception.InexistentWorkDayException;
import dev.jugapi.workcount.domain.exception.PolicyNotFoundException;
import dev.jugapi.workcount.domain.model.Clocking;
import dev.jugapi.workcount.domain.model.ClockingType;
import dev.jugapi.workcount.domain.model.DailyPolicy;
import dev.jugapi.workcount.domain.model.WorkDay;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class ClockingService implements ClockInUseCase, CreateClockingUseCase,
        UpdateClockingUseCase, DeleteClockingUseCase {

    private final WorkDayRepository workDayRepository;
    private final DailyPolicyRepository dailyPolicyRepository;

    public ClockingService(WorkDayRepository workDayRepository,
                           DailyPolicyRepository dailyPolicyRepository) {
        this.workDayRepository = workDayRepository;
        this.dailyPolicyRepository = dailyPolicyRepository;
    }

    @Override
    @Transactional
    public WorkDay clockIn() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        Optional<WorkDay> optWorkDay = workDayRepository.findByDate(today);

        WorkDay workDay = optWorkDay.orElseGet(() -> WorkDay.create(today));
        ClockingType clockingType = determineClockingType(workDay);

        workDay.addClocking(new Clocking(now, clockingType));
        workDay = calculateValidatedHoursAfterChangeClocking(workDay, today.getDayOfWeek());
        return workDayRepository.save(workDay);
    }

    @Override
    @Transactional
    public WorkDay createClocking(LocalDate date, LocalTime time, ClockingType clockingType) {
        Optional<WorkDay> optWorkDay = workDayRepository.findByDate(date);

        WorkDay workDay = optWorkDay.orElseGet(() -> WorkDay.create(date));

        workDay.addClocking(new Clocking(time, clockingType));
        workDay = calculateValidatedHoursAfterChangeClocking(workDay, date.getDayOfWeek());
        return workDayRepository.save(workDay);
    }

    @Override
    @Transactional
    public WorkDay updateClocking(LocalDate date, LocalTime originalTime, LocalTime newTime) {
        WorkDay workDay = workDayRepository.findByDate(date)
                .orElseThrow(() -> new InexistentWorkDayException(date));

        workDay.updateClocking(originalTime, newTime);
        workDay = calculateValidatedHoursAfterChangeClocking(workDay, date.getDayOfWeek());
        return workDayRepository.save(workDay);
    }

    @Override
    @Transactional
    public WorkDay deleteClockIn(LocalDate date, LocalTime time) {
        WorkDay workDay = workDayRepository.findByDate(date)
                .orElseThrow(() -> new InexistentWorkDayException(date));

        workDay.deleteClocking(time);
        workDay = calculateValidatedHoursAfterChangeClocking(workDay, date.getDayOfWeek());
        return workDayRepository.save(workDay);
    }

    private WorkDay calculateValidatedHoursAfterChangeClocking(WorkDay workDay, DayOfWeek day) {
        Optional<DailyPolicy> policy = dailyPolicyRepository.getPolicyFor(day);

        if (policy.isEmpty()) {
            throw new PolicyNotFoundException(day);
        }

        return workDay.calculateNetTimeWorkedAccordingToPolicy(policy.get());
    }

    private ClockingType determineClockingType(WorkDay workDay) {
        Optional<ClockingType> optType = workDay.getCurrentStatus();

        return optType.filter(clockingType -> clockingType.equals(ClockingType.IN))
                .map(clockingType -> ClockingType.OUT)
                .orElse(ClockingType.IN);
    }
}
