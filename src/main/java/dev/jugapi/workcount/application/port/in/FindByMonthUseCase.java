package dev.jugapi.workcount.application.port.in;

import dev.jugapi.workcount.domain.model.WorkDay;

import java.time.YearMonth;
import java.util.List;

public interface FindByMonthUseCase {
    List<WorkDay> findByMonth(YearMonth month);
}
