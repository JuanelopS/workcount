package dev.jugapi.workcount.domain.port.out;

import dev.jugapi.workcount.domain.model.WorkRegistration;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface WorkRegistrationRepository {
    void save(WorkRegistration registration);
    List<WorkRegistration> findByMonth(YearMonth month);
    boolean existsByWorkingDay(LocalDate date);
}
