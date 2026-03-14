package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.template;

import dev.jugapi.workcount.domain.model.WorkMonthTemplate;
import dev.jugapi.workcount.application.port.out.WorkMonthTemplateRepository;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.Optional;

@Component
public class WorkMonthTemplatePersistenceAdapter implements WorkMonthTemplateRepository {
    private final SpringDataWorkMonthTemplateRepository repository;
    private final WorkMonthTemplateMapper mapper;

    public WorkMonthTemplatePersistenceAdapter(SpringDataWorkMonthTemplateRepository repository,
                                               WorkMonthTemplateMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<WorkMonthTemplate> getWorkMonthTemplate(YearMonth yearMonth) {
        return Optional.ofNullable(
                repository.findByYearMonth(yearMonth)).map(mapper::toDomain);
    }
}
