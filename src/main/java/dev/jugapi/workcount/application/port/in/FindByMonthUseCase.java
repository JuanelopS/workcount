package dev.jugapi.workcount.application.port.in;

import dev.jugapi.workcount.domain.model.WorkRegistration;

import java.time.YearMonth;
import java.util.List;

public interface FindByMonthUseCase {
    List<WorkRegistration> findByMonth(YearMonth month);
}
