package dev.jugapi.workcount.infrastructure.adapter.in.web;

import dev.jugapi.workcount.domain.model.Clocking;

import java.time.LocalDate;
import java.util.List;

public record WorkDayWebResponse(
        LocalDate workingDay,
        List<Clocking> clockingList,
        double validatedHours) {
}
