package dev.jugapi.workcount.domain.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record DailyPolicy(
        DayOfWeek dayOfWeek,
        LocalTime limitEntryTime,
        LocalTime limitExitTime) {

    public LocalTime adjustEntry(LocalTime actualEntry) {
        return actualEntry.isBefore(limitEntryTime) ? limitEntryTime : actualEntry;
    }

    public LocalTime adjustExit(LocalTime actualExit) {
        return actualExit.isAfter(limitExitTime) ? limitExitTime : actualExit;
    }
}
