package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.registration;

import dev.jugapi.workcount.domain.model.WorkDay;
import org.springframework.stereotype.Component;

@Component
public class WorkRegistrationMapper {

    public WorkRegistrationEntity toEntity(WorkDay domain) {
        WorkRegistrationEntity entity = new WorkRegistrationEntity();
        entity.setId(null);
        entity.setWorkingDay(domain.getDay());
        entity.setStartTime(domain.getStartTime());
        entity.setFinishingTime(domain.getFinishingTime());
        entity.setBreakDuration(domain.getBreakDuration());
        entity.setValidatedHours(domain.getValidatedHours());
        return entity;
    }

    public WorkDay toDomain(WorkRegistrationEntity entity) {
        return WorkDay.of(
                entity.getWorkingDay(),
                entity.getStartTime(),
                entity.getFinishingTime(),
                entity.getBreakDuration(),
                entity.getValidatedHours()
        );
    }
}
