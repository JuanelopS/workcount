package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.registration;

import dev.jugapi.workcount.application.port.out.WorkDayRepository;
import dev.jugapi.workcount.domain.exception.InexistentWorkDayException;
import dev.jugapi.workcount.domain.model.WorkDay;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public class WorkDayPersistenceAdapter implements WorkDayRepository {
    private final SpringDataWorkRegistrationRepository repository;
    private final WorkDayMapper mapper;

    public WorkDayPersistenceAdapter(SpringDataWorkRegistrationRepository repository,
                                     WorkDayMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public WorkDay save(WorkDay workDay) {
        WorkDayEntity entity = mapper.toEntity(workDay);

        // upsert logic (id != null ? update : insert)
        Optional<WorkDayEntity> existing = repository
                .findByWorkingDay(workDay.getDate());

        existing.ifPresent(workRegistrationEntity
                -> entity.setId(workRegistrationEntity.getId()));

        WorkDayEntity result = repository.save(entity);
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
        List<WorkDayEntity> list = repository.findByWorkingDayBetween(firstDay, lastDay);
        return list.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean exists(LocalDate date) {
        return repository.existsByWorkingDay(date);
    }
}
