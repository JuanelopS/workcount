package dev.jugapi.workcount.domain.exception;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InexistentRegisteredDayException extends RuntimeException {
    public InexistentRegisteredDayException(LocalDate date) {
        super("No existen registros para el día " +
                date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    }
}
