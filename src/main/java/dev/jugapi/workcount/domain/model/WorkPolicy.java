package dev.jugapi.workcount.domain.model;

import java.time.LocalTime;

public class WorkPolicy {
    private LocalTime limitEntryTime;
    private LocalTime limitExitTime;

    public WorkPolicy(LocalTime limitEntryTime, LocalTime limitExitTime) {
        this.limitEntryTime = limitEntryTime;
        this.limitExitTime = limitExitTime;
    }

    public LocalTime getLimitEntryTime() {
        return limitEntryTime;
    }

    public LocalTime getLimitExitTime() {
        return limitExitTime;
    }
}
