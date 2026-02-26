package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.registration;

import dev.jugapi.workcount.domain.model.WorkRegistration;
import org.springframework.stereotype.Component;

@Component
public class WorkRegistrationMapper {

    public WorkRegistrationEntity toEntity(WorkRegistration wr) {
        WorkRegistrationEntity entity = new WorkRegistrationEntity();
        entity.setId(null);
        entity.setWorkingDay(wr.getWorkingDay());
        entity.setStartTime(wr.getStartTime());
        entity.setFinishingTime(wr.getFinishingTime());
        entity.setBreakDuration(wr.getBreakDuration());
        entity.setValidatedHours(wr.getValidatedHours());
        return entity;
    }

    public WorkRegistration toDomain(WorkRegistrationEntity wre) {
        return new WorkRegistration(
                wre.getWorkingDay(),
                wre.getStartTime(),
                wre.getFinishingTime(),
                wre.getBreakDuration(),
                wre.getValidatedHours()
        );
    }
}
