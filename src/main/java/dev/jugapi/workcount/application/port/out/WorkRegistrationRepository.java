package dev.jugapi.workcount.application.port.out;

import dev.jugapi.workcount.domain.model.WorkRegistration;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface WorkRegistrationRepository {
    WorkRegistration save(WorkRegistration registration);
    void deleteByWorkingDay(LocalDate workingDay);
    List<WorkRegistration> findByMonth(YearMonth month);
    boolean existsByWorkingDay(LocalDate date);
}
