package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.policy;

import dev.jugapi.workcount.domain.model.policy.DailyPolicy;
import org.springframework.stereotype.Component;

@Component
public class DailyPolicyPersistenceMapper {

    public DailyPolicyEntity toEntity(DailyPolicy domain) {
        DailyPolicyEntity entity = new DailyPolicyEntity();
        entity.setId(null);
        entity.setDayOfWeek(domain.dayOfWeek());
        entity.setLimitEntryTime(domain.limitEntryTime());
        entity.setLimitExitTime(domain.limitExitTime());
        return entity;
    }

    public DailyPolicy toDomain(DailyPolicyEntity entity) {
        return new DailyPolicy(
                entity.getDayOfWeek(),
                entity.getLimitEntryTime(),
                entity.getLimitExitTime()
        );
    }
}
