package dev.jugapi.workcount.domain.exception;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

public class PolicyNotFoundException extends RuntimeException {

    public PolicyNotFoundException(DayOfWeek dayOfWeek) {
        System.out.println("No existe una política de horarios para el día " +
                dayOfWeek.getDisplayName(TextStyle.FULL, new Locale("es", "ES")));
    }
}
