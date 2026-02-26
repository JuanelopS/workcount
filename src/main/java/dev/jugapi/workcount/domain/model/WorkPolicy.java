package dev.jugapi.workcount.domain.model;

import java.time.LocalTime;

public record WorkPolicy(
        LocalTime limitEntryTime,
        LocalTime limitExitTime) {
}
