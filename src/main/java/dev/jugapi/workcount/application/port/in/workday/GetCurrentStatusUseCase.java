package dev.jugapi.workcount.application.port.in.workday;

import dev.jugapi.workcount.domain.model.ClockingType;

import java.util.Optional;

public interface GetCurrentStatusUseCase {
    Optional<ClockingType> getCurrentStatus();
}
