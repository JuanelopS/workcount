package dev.jugapi.workcount.application.port.in;

import java.time.LocalDate;

public interface DeleteWorkDayUseCase {
    void deleteByWorkingDay(LocalDate date);
}
