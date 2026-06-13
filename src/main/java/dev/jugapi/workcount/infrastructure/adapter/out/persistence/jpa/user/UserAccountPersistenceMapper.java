package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.user;

import dev.jugapi.workcount.domain.model.user.UserAccount;
import org.springframework.stereotype.Component;

@Component
public class UserAccountPersistenceMapper {

    public UserAccountEntity toEntity(UserAccount domain) {
        return new UserAccountEntity(
                domain.getUsername(),
                domain.getPasswordHash(),
                domain.isEnabled()
        );
    }

    public UserAccount toDomain(UserAccountEntity entity) {
        return UserAccount.of(
                entity.getUsername(),
                entity.getPasswordHash(),
                entity.isEnabled()
        );
    }
}
