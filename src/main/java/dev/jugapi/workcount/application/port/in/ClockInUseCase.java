package dev.jugapi.workcount.application.port.in;

import dev.jugapi.workcount.domain.model.ClockingType;
import dev.jugapi.workcount.domain.model.WorkDay;

public interface ClockInUseCase {
    WorkDay clockIn(ClockingType type);
}
