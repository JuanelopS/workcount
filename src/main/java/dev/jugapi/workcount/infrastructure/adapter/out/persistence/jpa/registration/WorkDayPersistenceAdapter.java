package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.registration;

import dev.jugapi.workcount.application.port.out.workday.WorkDayRepository;
import dev.jugapi.workcount.domain.exception.InexistentWorkDayException;
import dev.jugapi.workcount.domain.model.workday.WorkDay;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public class WorkDayPersistenceAdapter implements WorkDayRepository {
    private final SpringDataWorkRegistrationRepository repository;
    private final WorkDayPersistenceMapper mapper;

    public WorkDayPersistenceAdapter(SpringDataWorkRegistrationRepository repository,
                                     WorkDayPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public WorkDay save(WorkDay workDay) {
        WorkDayPersistenceEntity entity = mapper.toEntity(workDay);

        // upsert logic (id != null ? update : insert)
        Optional<WorkDayPersistenceEntity> existing = repository
                .findByDate(workDay.getDate());

        existing.ifPresent(workRegistrationEntity
                -> entity.setId(workRegistrationEntity.getId()));

        WorkDayPersistenceEntity result = repository.save(entity);
        return mapper.toDomain(result);
    }

    @Override
    public void delete(LocalDate workingDay) {
        repository.findByDate(workingDay).ifPresentOrElse(
                repository::delete,
                () -> {
                    throw new InexistentWorkDayException(workingDay);
                }
        );
    }

    @Override
    public Optional<WorkDay> findByDate(LocalDate date) {
        return repository.findByDate(date)
                .map(mapper::toDomain);
    }

    @Override
    public List<WorkDay> findByDateBetween(LocalDate from, LocalDate to) {
        return repository.findByDateBetween(from, to).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<WorkDay> findByMonth(YearMonth month) {
        LocalDate firstDay = month.atDay(1);
        LocalDate lastDay = month.atEndOfMonth();
        return findByDateBetween(firstDay, lastDay);
    }

    @Override
    public boolean exists(LocalDate date) {
        return repository.existsByDate(date);
    }
}
