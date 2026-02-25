package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SpringDataWorkRegistrationRepository extends
        JpaRepository<WorkRegistrationEntity, Long> {

    List<WorkRegistrationEntity> findByWorkingDayBetween(LocalDate start, LocalDate end);
}
