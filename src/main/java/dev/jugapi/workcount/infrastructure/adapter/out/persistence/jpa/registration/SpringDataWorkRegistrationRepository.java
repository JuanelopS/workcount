package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.registration;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpringDataWorkRegistrationRepository extends
        JpaRepository<WorkRegistrationEntity, Long> {
    boolean existsByWorkingDay(LocalDate date);
    Optional<WorkRegistrationEntity> findByWorkingDay(LocalDate workingDay);
    List<WorkRegistrationEntity> findByWorkingDayBetween(LocalDate start, LocalDate end);
}
