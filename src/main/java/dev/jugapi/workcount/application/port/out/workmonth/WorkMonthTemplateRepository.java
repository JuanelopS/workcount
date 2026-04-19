package dev.jugapi.workcount.application.port.out.workmonth;

import dev.jugapi.workcount.domain.model.workmonth.WorkMonthTemplate;

import java.time.YearMonth;
import java.util.Optional;

public interface WorkMonthTemplateRepository {
    Optional<WorkMonthTemplate> getWorkMonthTemplate(YearMonth yearMonth);
}
