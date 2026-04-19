package dev.jugapi.workcount.application.port.in.clocking;

import java.time.LocalDate;
import java.time.LocalTime;

public interface DeleteClockingUseCase {
    void deleteClocking(LocalDate date, LocalTime time);
}
