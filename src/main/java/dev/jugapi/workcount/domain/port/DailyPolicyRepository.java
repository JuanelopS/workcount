package dev.jugapi.workcount.domain.port;

import dev.jugapi.workcount.domain.model.DailyPolicy;

import java.time.DayOfWeek;
import java.util.Optional;

public interface DailyPolicyRepository {
    Optional<DailyPolicy> getPolicyFor(DayOfWeek day);
}
