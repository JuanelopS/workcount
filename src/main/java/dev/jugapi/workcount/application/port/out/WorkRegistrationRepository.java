package dev.jugapi.workcount.application.port.out;

import dev.jugapi.workcount.domain.model.WorkDay;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface WorkRegistrationRepository {
    WorkDay save(WorkDay registration);
    void deleteByWorkingDay(LocalDate date);
    Optional<WorkDay> findByWorkingDay(LocalDate date);
    List<WorkDay> findByMonth(YearMonth month);
    boolean existsByWorkingDay(LocalDate date);
}
