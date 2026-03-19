package dev.jugapi.workcount.application.service;

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
public class ClockingService implements CreateClockingUseCase, UpdateClockingUseCase,
        DeleteClockingUseCase {

    private final WorkDayRepository workDayRepository;
    private final DailyPolicyRepository dailyPolicyRepository;

    public ClockingService(WorkDayRepository workDayRepository,
                           DailyPolicyRepository dailyPolicyRepository) {
        this.workDayRepository = workDayRepository;
        this.dailyPolicyRepository = dailyPolicyRepository;
    }

    @Override
    @Transactional
    public WorkDay createClocking(ClockingType type) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        Optional<WorkDay> optWorkDay = workDayRepository.findByDate(today);

        WorkDay workDay = optWorkDay.orElseGet(() -> WorkDay.create(today));

        Clocking clocking;
        if (workDay.getClockingList().isEmpty()) {
            clocking = new Clocking(now, ClockingType.IN);
        }
        clocking = new Clocking(now, type);

        workDay.addClocking(clocking);
        workDay = calculateValidatedHoursAfterChangeClocking(workDay, today.getDayOfWeek());
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

        return workDay.calculateValidatedHoursAccordingToPolicy(policy.get());
    }
}
