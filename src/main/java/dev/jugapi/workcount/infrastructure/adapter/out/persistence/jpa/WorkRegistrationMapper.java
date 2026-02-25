package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa;

import dev.jugapi.workcount.domain.model.WorkRegistration;
import org.springframework.stereotype.Component;

@Component
public class WorkRegistrationMapper {

    public WorkRegistrationEntity toEntity(WorkRegistration domainObject) {
        WorkRegistrationEntity entity = new WorkRegistrationEntity();
        entity.setId(null);
        entity.setWorkingDay(domainObject.getWorkingDay());
        entity.setStartTime(domainObject.getStartTime());
        entity.setFinishingTime(domainObject.getFinishingTime());
        entity.setBreakDuration(domainObject.getBreakDuration());
        entity.setValidatedHours(domainObject.getValidatedHours());
        return entity;
    }

    public WorkRegistration toDomain(WorkRegistrationEntity entityObject) {
        return new WorkRegistration(
                entityObject.getWorkingDay(),
                entityObject.getStartTime(),
                entityObject.getFinishingTime(),
                entityObject.getBreakDuration(),
                entityObject.getValidatedHours()
        );
    }
}
