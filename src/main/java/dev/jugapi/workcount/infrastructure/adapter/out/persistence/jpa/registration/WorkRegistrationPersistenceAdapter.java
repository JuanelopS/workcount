package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.registration;

import dev.jugapi.workcount.domain.exception.InexistentWorkDayException;
import dev.jugapi.workcount.domain.model.WorkDay;
import dev.jugapi.workcount.application.port.out.WorkDayRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public class WorkRegistrationPersistenceAdapter implements WorkDayRepository {
    private final SpringDataWorkRegistrationRepository repository;
    private final WorkRegistrationMapper mapper;

    public WorkRegistrationPersistenceAdapter(SpringDataWorkRegistrationRepository repository,
                                              WorkRegistrationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public WorkDay save(WorkDay workDay) {
        WorkRegistrationEntity entity = mapper.toEntity(workDay);

        // upsert logic (id != null ? update : insert)
        Optional<WorkRegistrationEntity> existing = repository
                .findByWorkingDay(workDay.getDay());

        existing.ifPresent(workRegistrationEntity
                -> entity.setId(workRegistrationEntity.getId()));

        WorkRegistrationEntity result = repository.save(entity);
        return mapper.toDomain(result);
    }

    @Override
    public void delete(LocalDate workingDay) {
        repository.findByWorkingDay(workingDay).ifPresentOrElse(
                repository::delete,
                () -> {
                    throw new InexistentWorkDayException(workingDay);
                }
        );
    }

    @Override
    public Optional<WorkDay> findByDate(LocalDate date) {
        return repository.findByWorkingDay(date)
                .map(mapper::toDomain);
    }

    @Override
    public List<WorkDay> findByMonth(YearMonth month) {
        LocalDate firstDay = month.atDay(1);
        LocalDate lastDay = month.atEndOfMonth();
        List<WorkRegistrationEntity> list = repository.findByWorkingDayBetween(firstDay, lastDay);
        return list.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean exists(LocalDate date) {
        return repository.existsByWorkingDay(date);
    }
}
