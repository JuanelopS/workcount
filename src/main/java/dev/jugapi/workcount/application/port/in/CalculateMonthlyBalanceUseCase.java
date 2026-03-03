package dev.jugapi.workcount.application.port.in;

import java.time.Duration;
import java.time.YearMonth;

public interface CalculateMonthlyBalanceUseCase {
    Duration calculateMonthlyBalance(YearMonth month);
}
