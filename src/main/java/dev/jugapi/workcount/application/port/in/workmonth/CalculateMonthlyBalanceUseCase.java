package dev.jugapi.workcount.application.port.in.workmonth;

import java.time.Duration;
import java.time.YearMonth;

public interface CalculateMonthlyBalanceUseCase {
    Duration calculateMonthlyBalance(YearMonth month);
}
