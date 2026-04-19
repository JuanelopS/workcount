package dev.jugapi.workcount.application.port.in.clocking;

import dev.jugapi.workcount.domain.model.clocking.ClockingType;
import dev.jugapi.workcount.domain.model.workday.WorkDay;

import java.time.LocalDate;
import java.time.LocalTime;

public interface UpdateClockingUseCase {
    WorkDay updateClocking(LocalDate date, LocalTime originalTime, LocalTime newTime,
                           ClockingType clockingType);
}
