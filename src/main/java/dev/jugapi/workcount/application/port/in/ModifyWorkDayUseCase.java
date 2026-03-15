package dev.jugapi.workcount.application.port.in;

import dev.jugapi.workcount.domain.model.WorkRegistration;

public interface ModifyWorkDayUseCase {
    WorkRegistration modifyWorkDay(WorkRegistration wr);
}
