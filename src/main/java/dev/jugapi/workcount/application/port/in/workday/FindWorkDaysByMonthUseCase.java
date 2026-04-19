package dev.jugapi.workcount.application.port.in.workday;

import dev.jugapi.workcount.domain.model.WorkDay;

import java.time.YearMonth;
import java.util.List;

public interface FindWorkDaysByMonthUseCase {
    List<WorkDay> findWorkDaysByMonth(YearMonth month);
}
