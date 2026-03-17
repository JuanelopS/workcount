package dev.jugapi.workcount.application.port.in;

import dev.jugapi.workcount.domain.model.WorkDay;

public interface CreateWorkDayUseCase {
    WorkDay createWorkDay(WorkDay wd);
}
