package dev.jugapi.workcount.application.port.in.workday;

import java.time.LocalDate;

public interface DeleteWorkDayUseCase {
    void deleteWorkDay(LocalDate date);
}
