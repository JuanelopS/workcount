package dev.jugapi.workcount.application.port.in.workday;

import dev.jugapi.workcount.domain.model.WorkDay;

public interface CreateWorkDayUseCase {
    WorkDay createWorkDay(WorkDay wd);
}
