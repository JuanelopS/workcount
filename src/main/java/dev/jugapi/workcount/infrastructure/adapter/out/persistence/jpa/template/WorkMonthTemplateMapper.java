package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.template;

import dev.jugapi.workcount.domain.model.WorkMonthTemplate;
import org.springframework.stereotype.Component;

@Component
public class WorkMonthTemplateMapper {

    public WorkMonthTemplateEntity toEntity(WorkMonthTemplate wmt) {
        WorkMonthTemplateEntity entity = new WorkMonthTemplateEntity();
        entity.setId(null);
        entity.setYearMonth(wmt.yearMonth());
        entity.setWeeks(wmt.weeks());
        return entity;
    }

    public WorkMonthTemplate toDomain(WorkMonthTemplateEntity wmte) {
        return new WorkMonthTemplate(
                wmte.getYearMonth(),
                wmte.getWeeks()
        );
    }
}
