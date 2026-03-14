package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.registration;

import dev.jugapi.workcount.domain.exception.InexistentRegisteredDay;
import dev.jugapi.workcount.domain.model.WorkRegistration;
import dev.jugapi.workcount.application.port.out.WorkRegistrationRepository;
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
    public WorkRegistration save(WorkRegistration registration) {
        WorkRegistrationEntity entity = mapper.toEntity(registration);
        WorkRegistrationEntity result = repository.save(entity);
        return mapper.toDomain(result);
    }

    @Override
    public void deleteByWorkingDay(LocalDate workingDay) {
        repository.findByWorkingDay(workingDay).ifPresentOrElse(
                repository::delete,
                () -> {
                    throw new InexistentRegisteredDay(workingDay);
                }
        );
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

    @Override
    public boolean existsByWorkingDay(LocalDate date) {
        return repository.existsByWorkingDay(date);
    }
}
