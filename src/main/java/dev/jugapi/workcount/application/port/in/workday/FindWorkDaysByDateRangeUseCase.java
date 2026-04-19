package dev.jugapi.workcount.application.port.in.workday;

import dev.jugapi.workcount.domain.model.workday.WorkDay;

import java.time.LocalDate;
import java.util.List;

public interface FindWorkDaysByDateRangeUseCase {
    List<WorkDay> findWorkDaysByDateRange(LocalDate from, LocalDate to);
}

