package dev.jugapi.workcount.application.port.in.clocking;

import dev.jugapi.workcount.domain.model.WorkDay;

import java.time.LocalDate;
import java.time.LocalTime;

public interface DeleteClockingUseCase {
    WorkDay deleteClockIn(LocalDate date, LocalTime time);
}
