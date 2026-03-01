package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.template;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.YearMonth;

public interface SpringDataWorkMonthTemplateRepository extends
        JpaRepository<WorkMonthTemplateEntity, Long> {
    @Query("SELECT t FROM WorkMonthTemplateEntity t WHERE t.yearMonth = :ym")
    WorkMonthTemplateEntity findByYearMonth(@Param("ym") YearMonth ym);
}
