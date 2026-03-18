package dev.jugapi.workcount.application.service;

import dev.jugapi.workcount.application.port.in.clocking.CreateClockingUseCase;
import dev.jugapi.workcount.application.port.in.clocking.DeleteClockingUseCase;
import dev.jugapi.workcount.application.port.in.clocking.UpdateClockingUseCase;
import dev.jugapi.workcount.application.port.out.WorkDayRepository;
import dev.jugapi.workcount.domain.exception.InexistentWorkDayException;
import dev.jugapi.workcount.domain.model.Clocking;
import dev.jugapi.workcount.domain.model.ClockingType;
import dev.jugapi.workcount.domain.model.WorkDay;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class ClockingService implements CreateClockingUseCase, UpdateClockingUseCase,
        DeleteClockingUseCase {

    private final WorkDayRepository workDayRepository;

    public ClockingService(WorkDayRepository workDayRepository) {
        this.workDayRepository = workDayRepository;
    }

    @Override
    @Transactional
    public WorkDay createClocking(ClockingType type) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        Optional<WorkDay> optWorkDay = workDayRepository.findByDate(today);

        WorkDay workDay = optWorkDay.orElseGet(() -> WorkDay.create(today));

        Clocking clocking = new Clocking(now, type);
        workDay.addClocking(clocking);

        return workDayRepository.save(workDay);
    }

    @Override
    @Transactional
    public void updateClocking(LocalDate date, LocalTime originalTime, LocalTime newTime) {
        WorkDay workDay = workDayRepository.findByDate(date)
                .orElseThrow(() -> new InexistentWorkDayException(date));

        workDay.updateClocking(originalTime, newTime);
    }

    @Override
    @Transactional
    public void deleteClockIn(LocalDate date, LocalTime time) {
        WorkDay workDay = workDayRepository.findByDate(date)
                .orElseThrow(() -> new InexistentWorkDayException(date));

        workDay.deleteClocking();

    }
}
