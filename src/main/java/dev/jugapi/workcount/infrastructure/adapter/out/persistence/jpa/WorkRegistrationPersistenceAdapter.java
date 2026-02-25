package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa;

import dev.jugapi.workcount.domain.model.WorkRegistration;
import dev.jugapi.workcount.domain.port.WorkRegistrationRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Repository
public class WorkRegistrationPersistenceAdapter implements WorkRegistrationRepository {
    private final SpringDataWorkRegistrationRepository repository;
    private final WorkRegistrationMapper mapper;

    public WorkRegistrationPersistenceAdapter(SpringDataWorkRegistrationRepository repository,
                                              WorkRegistrationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void save(WorkRegistration registration) {
        WorkRegistrationEntity entity = mapper.toEntity(registration);
        repository.save(entity);
    }

    @Override
    public List<WorkRegistration> findByMonth(YearMonth month) {
        LocalDate firstDay = month.atDay(1);
        LocalDate lastDay = month.atEndOfMonth();
        List<WorkRegistrationEntity> list = repository.findByWorkingDayBetween(firstDay, lastDay);
        return list.stream()
                .map(mapper::toDomain)
                .toList();
    }
}
