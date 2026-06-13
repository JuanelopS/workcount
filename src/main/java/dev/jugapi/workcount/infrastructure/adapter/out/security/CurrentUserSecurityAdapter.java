package dev.jugapi.workcount.infrastructure.adapter.out.security;

import dev.jugapi.workcount.application.port.out.security.CurrentUserPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CurrentUserSecurityAdapter implements CurrentUserPort {

    @Override
    public Optional<String> getCurrentUsername() {
        return Optional.empty();
    }
}
