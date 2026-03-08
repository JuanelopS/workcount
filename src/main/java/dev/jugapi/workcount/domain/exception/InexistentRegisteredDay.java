package dev.jugapi.workcount.domain.exception;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InexistentRegisteredDay extends RuntimeException {
    public InexistentRegisteredDay(LocalDate date) {
        super("No existen registros para el día " +
                date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    }
}
