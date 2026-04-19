package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.template;

import dev.jugapi.workcount.domain.model.workmonth.WorkMonthTemplate;
import org.springframework.stereotype.Component;

@Component
public class WorkMonthTemplateMapper {

    public WorkMonthTemplateEntity toEntity(WorkMonthTemplate domain) {
        WorkMonthTemplateEntity entity = new WorkMonthTemplateEntity();
        entity.setId(null);
        entity.setYearMonth(domain.yearMonth());
        entity.setWeeks(domain.weeks());
        return entity;
    }

    public WorkMonthTemplate toDomain(WorkMonthTemplateEntity entity) {
        return new WorkMonthTemplate(
                entity.getYearMonth(),
                entity.getWeeks()
        );
    }
}
