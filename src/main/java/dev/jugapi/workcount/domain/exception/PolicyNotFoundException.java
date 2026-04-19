package dev.jugapi.workcount.domain.exception;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class PolicyNotFoundException extends RuntimeException {
    public PolicyNotFoundException(DayOfWeek dayOfWeek, LocalDate date) {
        super("No existe una política de horarios para el día " +
                date +
                " (" +
                dayOfWeek.getDisplayName(TextStyle.FULL, new Locale("es", "ES")) +
                ")");
    }
}
