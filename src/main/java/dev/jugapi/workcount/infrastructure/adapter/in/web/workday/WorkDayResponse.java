package dev.jugapi.workcount.infrastructure.adapter.in.web.workday;

import dev.jugapi.workcount.domain.model.Clocking;

import java.time.LocalDate;
import java.util.List;

public record WorkDayResponse(
        LocalDate workingDay,
        List<Clocking> clockingList,
        double validatedHours) {
}
