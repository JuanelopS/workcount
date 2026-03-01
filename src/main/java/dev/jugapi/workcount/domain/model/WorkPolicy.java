package dev.jugapi.workcount.domain.model;

import java.time.DayOfWeek;
import java.util.Map;

public class WorkPolicy {
    private final Map<DayOfWeek, DailyPolicy> rules;

    public WorkPolicy(Map<DayOfWeek, DailyPolicy> rules) {
        this.rules = rules;
    }

    public DailyPolicy getPolicyFor(DayOfWeek day){
        return rules.get(day);
    }
}
