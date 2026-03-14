package dev.jugapi.workcount.application.port.out;

import dev.jugapi.workcount.domain.model.DailyPolicy;

import java.time.DayOfWeek;
import java.util.Optional;

public interface DailyPolicyRepository {
    Optional<DailyPolicy> getPolicyFor(DayOfWeek day);
}
