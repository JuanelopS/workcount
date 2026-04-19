package dev.jugapi.workcount.application.port.in.workday;

import dev.jugapi.workcount.domain.model.workday.WorkDay;

public interface UpdateWorkDayUseCase {
    WorkDay updateWorkDay(WorkDay wr);
}
