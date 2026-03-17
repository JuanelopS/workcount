package dev.jugapi.workcount.application.port.in.clocking;

import dev.jugapi.workcount.domain.model.ClockingType;
import dev.jugapi.workcount.domain.model.WorkDay;

public interface CreateClockingUseCase {
    WorkDay createClocking(ClockingType type);
}
