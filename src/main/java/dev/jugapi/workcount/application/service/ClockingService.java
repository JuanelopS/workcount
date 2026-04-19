package dev.jugapi.workcount.application.service;

import dev.jugapi.workcount.application.port.in.clocking.ClockInUseCase;
import dev.jugapi.workcount.application.port.in.clocking.CreateClockingUseCase;
import dev.jugapi.workcount.application.port.in.clocking.DeleteClockingUseCase;
import dev.jugapi.workcount.application.port.in.clocking.UpdateClockingUseCase;
import dev.jugapi.workcount.application.port.out.policy.DailyPolicyRepository;
import dev.jugapi.workcount.application.port.out.workday.WorkDayRepository;
import dev.jugapi.workcount.domain.exception.InexistentWorkDayException;
import dev.jugapi.workcount.domain.exception.PolicyNotFoundException;
import dev.jugapi.workcount.domain.model.clocking.Clocking;
import dev.jugapi.workcount.domain.model.clocking.ClockingType;
import dev.jugapi.workcount.domain.model.policy.DailyPolicy;
import dev.jugapi.workcount.domain.model.workday.WorkDay;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class ClockingService implements ClockInUseCase, CreateClockingUseCase,
        UpdateClockingUseCase, DeleteClockingUseCase {

    private final WorkDayRepository workDayRepository;
    private final DailyPolicyRepository dailyPolicyRepository;
    private final Clock clock;

    public ClockingService(WorkDayRepository workDayRepository,
                           DailyPolicyRepository dailyPolicyRepository,
                           Clock clock) {
        this.workDayRepository = workDayRepository;
        this.dailyPolicyRepository = dailyPolicyRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public WorkDay clockIn() {
        LocalDate today = LocalDate.now(clock);
        LocalTime now = LocalTime.now(clock);
        Optional<WorkDay> optWorkDay = workDayRepository.findByDate(today);

        WorkDay workDay = optWorkDay.orElseGet(() -> WorkDay.create(today));
        ClockingType clockingType = determineClockingType(workDay);

        workDay.addClocking(new Clocking(now, clockingType));
        workDay = calculateNetTimeWorkedAfterChangeClocking(workDay, today.getDayOfWeek());
        return workDayRepository.save(workDay);
    }

    @Override
    @Transactional
    public WorkDay createClocking(LocalDate date, LocalTime time, ClockingType clockingType) {
        Optional<WorkDay> optWorkDay = workDayRepository.findByDate(date);

        WorkDay workDay = optWorkDay.orElseGet(() -> WorkDay.create(date));

        workDay.addClocking(new Clocking(time, clockingType));
        workDay = calculateNetTimeWorkedAfterChangeClocking(workDay, date.getDayOfWeek());
        return workDayRepository.save(workDay);
    }

    @Override
    @Transactional
    public WorkDay updateClocking(LocalDate date, LocalTime originalTime, LocalTime newTime,
                                  ClockingType clockingType) {
        WorkDay workDay = workDayRepository.findByDate(date)
                .orElseThrow(() -> new InexistentWorkDayException(date));

        workDay.updateClocking(originalTime, newTime, clockingType);
        workDay = calculateNetTimeWorkedAfterChangeClocking(workDay, date.getDayOfWeek());
        return workDayRepository.save(workDay);
    }

    @Override
    @Transactional
    public void deleteClocking(LocalDate date, LocalTime time) {
        WorkDay workDay = workDayRepository.findByDate(date)
                .orElseThrow(() -> new InexistentWorkDayException(date));

        workDay.deleteClocking(time);
        workDay = calculateNetTimeWorkedAfterChangeClocking(workDay, date.getDayOfWeek());
        workDayRepository.save(workDay);
    }

    private WorkDay calculateNetTimeWorkedAfterChangeClocking(WorkDay workDay, DayOfWeek day) {
        Optional<DailyPolicy> policy = dailyPolicyRepository.getPolicyFor(day);

        if (policy.isEmpty()) {
            throw new PolicyNotFoundException(day, workDay.getDate());
        }

        return workDay.calculateNetTimeWorked(policy.get());
    }

    private ClockingType determineClockingType(WorkDay workDay) {
        Optional<ClockingType> optType = workDay.getCurrentStatus();

        return optType.filter(clockingType -> clockingType.equals(ClockingType.IN))
                .map(clockingType -> ClockingType.OUT)
                .orElse(ClockingType.IN);
    }
}
