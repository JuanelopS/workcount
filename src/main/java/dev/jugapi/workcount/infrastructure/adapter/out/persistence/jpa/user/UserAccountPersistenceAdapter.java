package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.user;


import dev.jugapi.workcount.application.port.out.user.UserAccountRepository;
import dev.jugapi.workcount.domain.model.user.UserAccount;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserAccountPersistenceAdapter implements UserAccountRepository {

    private final SpringDataUserAccountRepository repository;
    private final UserAccountPersistenceMapper mapper;

    public UserAccountPersistenceAdapter(SpringDataUserAccountRepository repository,
                                         UserAccountPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public UserAccount save(UserAccount userAccount) {
        UserAccountEntity entity = mapper.toEntity(userAccount);

        // upsert (domain without id)
        repository.findByUsername(userAccount.getUsername())
                .ifPresent(existing -> entity.setId(existing.getId()));

        UserAccountEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        return repository.findByUsername(username)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }
}
