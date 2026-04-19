package dev.jugapi.workcount.application.port.out.security;

import java.util.Optional;

public interface CurrentUserPort {
    Optional<String> getCurrentUsername();
}
