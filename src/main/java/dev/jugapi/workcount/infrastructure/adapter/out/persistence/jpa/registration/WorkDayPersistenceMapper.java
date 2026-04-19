package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.registration;

import dev.jugapi.workcount.domain.model.Clocking;
import dev.jugapi.workcount.domain.model.WorkDay;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkDayPersistenceMapper {

    public WorkDayPersistenceEntity toEntity(WorkDay domain) {
        WorkDayPersistenceEntity entity = new WorkDayPersistenceEntity();
        entity.setId(null);
        entity.setDate(domain.getDate());
        entity.setClockingList(toEmbeddableList(domain.getClockingList()));
        entity.setValidatedHours(domain.getNetTimeWorked());
        return entity;
    }

    public WorkDay toDomain(WorkDayPersistenceEntity entity) {
        return WorkDay.of(
                entity.getDate(),
                toDomainList(entity.getClockingList()),
                entity.getValidatedHours()
        );
    }

    private List<ClockingEmbeddable> toEmbeddableList(List<Clocking> list) {
        return list.stream()
                .map(c -> new ClockingEmbeddable(c.time(), c.type()))
                .toList();
    }

    private List<Clocking> toDomainList(List<ClockingEmbeddable> list) {
        return list.stream()
                .map(c -> new Clocking(c.getTime(), c.getType()))
                .toList();
    }
}
