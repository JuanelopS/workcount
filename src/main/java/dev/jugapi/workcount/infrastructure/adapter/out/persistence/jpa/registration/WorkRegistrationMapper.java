package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.registration;

import dev.jugapi.workcount.domain.model.WorkRegistration;
import org.springframework.stereotype.Component;

@Component
public class WorkRegistrationMapper {

    public WorkRegistrationEntity toEntity(WorkRegistration domain) {
        WorkRegistrationEntity entity = new WorkRegistrationEntity();
        entity.setId(null);
        entity.setWorkingDay(domain.getWorkingDay());
        entity.setStartTime(domain.getStartTime());
        entity.setFinishingTime(domain.getFinishingTime());
        entity.setBreakDuration(domain.getBreakDuration());
        entity.setValidatedHours(domain.getValidatedHours());
        return entity;
    }

    public WorkRegistration toDomain(WorkRegistrationEntity entity) {
        return WorkRegistration.of(
                entity.getWorkingDay(),
                entity.getStartTime(),
                entity.getFinishingTime(),
                entity.getBreakDuration(),
                entity.getValidatedHours()
        );
    }
}
