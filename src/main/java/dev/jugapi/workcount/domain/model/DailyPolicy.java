package dev.jugapi.workcount.domain.model;

import java.time.LocalTime;

public record DailyPolicy(
        LocalTime limitEntryTime,
        LocalTime limitExitTime) {

    public LocalTime adjustEntry(LocalTime actualEntry) {
        return actualEntry.isBefore(limitEntryTime) ? limitEntryTime : actualEntry;
    }

    public LocalTime adjustExit(LocalTime actualExit) {
        return actualExit.isAfter(limitExitTime) ? limitExitTime : actualExit;
    }
}
