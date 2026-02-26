package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.template;

import dev.jugapi.workcount.domain.model.WorkMonthTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;

public interface SpringDataWorkMonthTemplateRepository extends
        JpaRepository<WorkMonthTemplateEntity, Long> {
    WorkMonthTemplateEntity findByYearMonth(YearMonth yearMonth);
}
