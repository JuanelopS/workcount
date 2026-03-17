package dev.jugapi.workcount.application.service;

import dev.jugapi.workcount.application.port.in.clocking.CreateClockingUseCase;
import dev.jugapi.workcount.application.port.in.clocking.DeleteClockingUseCase;
import dev.jugapi.workcount.application.port.in.clocking.UpdateClockingUseCase;
import dev.jugapi.workcount.application.port.out.WorkDayRepository;
import dev.jugapi.workcount.domain.model.Clocking;
import dev.jugapi.workcount.domain.model.ClockingType;
import dev.jugapi.workcount.domain.model.WorkDay;
import org.springframework.stereotype.Service;

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
    public void updateClocking(LocalDate date, LocalTime time) {
        if(!workDayRepository.findByDate(date))
    }

    @Override
    public void DeleteClockIn(LocalDate date, LocalTime time) {

    }
}
