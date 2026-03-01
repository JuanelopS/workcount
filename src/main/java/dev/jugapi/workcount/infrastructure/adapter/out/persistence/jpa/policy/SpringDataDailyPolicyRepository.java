package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.policy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.Optional;

public interface SpringDataDailyPolicyRepository extends JpaRepository<DailyPolicyEntity, Long> {
    Optional<DailyPolicyEntity> findByDayOfWeek(DayOfWeek day);
}
