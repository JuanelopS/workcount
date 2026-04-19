package dev.jugapi.workcount.domain.model.workmonth;

import java.time.Duration;
import java.time.YearMonth;

public record WorkMonthTemplate(
        YearMonth yearMonth,
        int weeks) {

    public Duration monthlyTargetHours(Duration weeklyHours) {
        return weeklyHours.multipliedBy(this.weeks);
    }
}
