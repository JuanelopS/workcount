package dev.jugapi.workcount.application.port.out.workday;

import dev.jugapi.workcount.domain.model.workday.WorkDay;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface WorkDayRepository {
    WorkDay save(WorkDay workDay);

    void delete(LocalDate date);

    boolean exists(LocalDate date);

    Optional<WorkDay> findByDate(LocalDate date);

    List<WorkDay> findByDateBetween(LocalDate from, LocalDate to);

    List<WorkDay> findByMonth(YearMonth month);
}
