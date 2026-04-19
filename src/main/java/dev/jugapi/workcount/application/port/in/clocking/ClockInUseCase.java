package dev.jugapi.workcount.application.port.in.clocking;

import dev.jugapi.workcount.domain.model.WorkDay;

public interface ClockInUseCase {
    WorkDay clockIn();
}
