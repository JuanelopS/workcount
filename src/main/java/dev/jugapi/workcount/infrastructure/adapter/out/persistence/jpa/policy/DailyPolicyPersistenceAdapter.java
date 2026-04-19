package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.policy;

import dev.jugapi.workcount.application.port.out.policy.DailyPolicyRepository;
import dev.jugapi.workcount.domain.model.policy.DailyPolicy;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.Optional;

@Component
public class DailyPolicyPersistenceAdapter implements DailyPolicyRepository {
    private final SpringDataDailyPolicyRepository repository;
    private final DailyPolicyPersistenceMapper mapper;

    public DailyPolicyPersistenceAdapter(SpringDataDailyPolicyRepository repository, DailyPolicyPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<DailyPolicy> getPolicyFor(DayOfWeek day) {
        return repository.findByDayOfWeek(day)
                .map(mapper::toDomain);
    }
}
