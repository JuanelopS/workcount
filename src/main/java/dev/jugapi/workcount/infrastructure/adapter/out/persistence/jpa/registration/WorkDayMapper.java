package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.registration;

import dev.jugapi.workcount.domain.model.WorkDay;
import org.springframework.stereotype.Component;

@Component
public class WorkDayMapper {

    public WorkDayEntity toEntity(WorkDay domain) {
        WorkDayEntity entity = new WorkDayEntity();
        entity.setId(null);
        entity.setDate(domain.getDate());
        entity.setClockingList(domain.getClockingList());
        entity.setValidatedHours(domain.getNetTimeWorked());
        return entity;
    }

    public WorkDay toDomain(WorkDayEntity entity) {
        return WorkDay.of(
                entity.getDate(),
                entity.getClockingList(),
                entity.getValidatedHours()
        );
    }
}
