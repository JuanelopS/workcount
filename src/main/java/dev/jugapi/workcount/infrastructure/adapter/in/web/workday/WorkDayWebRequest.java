package dev.jugapi.workcount.infrastructure.adapter.in.web.workday;

import dev.jugapi.workcount.domain.model.clocking.Clocking;

import java.time.LocalDate;
import java.util.List;

public record WorkDayWebRequest(
        LocalDate workingDay,
        List<Clocking> clockingList) {
}
