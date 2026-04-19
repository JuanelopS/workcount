package dev.jugapi.workcount.application.port.out.user;

import dev.jugapi.workcount.domain.model.user.UserAccount;

import java.util.Optional;

public interface UserAccountRepository {
    UserAccount save(UserAccount userAccount);

    Optional<UserAccount> findByUsername(String username);

    boolean existsByUsername(String username);
}
