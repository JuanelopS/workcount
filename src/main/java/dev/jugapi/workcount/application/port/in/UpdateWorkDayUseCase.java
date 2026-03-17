package dev.jugapi.workcount.application.port.in;

import dev.jugapi.workcount.domain.model.WorkDay;

public interface UpdateWorkDayUseCase {
    WorkDay updateWorkDay(WorkDay wr);
}
