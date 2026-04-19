package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.registration;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpringDataWorkRegistrationRepository extends
        JpaRepository<WorkDayPersistenceEntity, Long> {
    boolean existsByDate(LocalDate date);

    Optional<WorkDayPersistenceEntity> findByDate(LocalDate date);

    List<WorkDayPersistenceEntity> findByDateBetween(LocalDate start, LocalDate end);
}
