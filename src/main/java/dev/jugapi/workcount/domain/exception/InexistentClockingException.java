package dev.jugapi.workcount.domain.exception;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class InexistentClockingException extends RuntimeException {
    public InexistentClockingException(LocalTime time) {
        super("No existen ningún fichaje a las  " +
                time.format(DateTimeFormatter.ofPattern("HH:mm")));
    }
}
