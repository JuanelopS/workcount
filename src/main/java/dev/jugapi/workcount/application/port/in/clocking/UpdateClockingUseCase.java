package dev.jugapi.workcount.application.port.in.clocking;

import java.time.LocalDate;
import java.time.LocalTime;

public interface UpdateClockingUseCase {
    void updateClocking(LocalDate date, LocalTime originalTime, LocalTime newTime);
}
