package dev.jugapi.workcount.application.port.in;

import dev.jugapi.workcount.domain.model.WorkRegistration;

public interface RegisterWorkDayUseCase {
    void registerWorkDay(WorkRegistration workRegistration);
}
