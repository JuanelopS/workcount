package dev.jugapi.workcount.application.port.out;

import dev.jugapi.workcount.domain.model.WorkDay;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface WorkRegistrationRepository {
    WorkDay save(WorkDay registration);
    void deleteByWorkingDay(LocalDate workingDay);
    List<WorkDay> findByMonth(YearMonth month);
    boolean existsByWorkingDay(LocalDate date);
}
