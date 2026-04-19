package dev.jugapi.workcount.application.port.in.workday;

import dev.jugapi.workcount.domain.model.workday.WorkDay;

import java.time.LocalDate;

public interface FindWorkDayByDateUseCase {
    WorkDay findWorkDayByDate(LocalDate date);
}

