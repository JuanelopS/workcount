package dev.jugapi.workcount.application.port.out.policy;

import dev.jugapi.workcount.domain.model.policy.DailyPolicy;

import java.time.DayOfWeek;
import java.util.Optional;

public interface DailyPolicyRepository {
    Optional<DailyPolicy> getPolicyFor(DayOfWeek day);
}
