package dev.jugapi.workcount.domain.port;

import dev.jugapi.workcount.domain.model.WorkMonthTemplate;

import java.time.YearMonth;
import java.util.Optional;

public interface WorkMonthTemplateRepository {
    Optional<WorkMonthTemplate> getWorkMonthTemplate(YearMonth yearMonth);
}
