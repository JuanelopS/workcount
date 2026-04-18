package dev.jugapi.workcount.domain.exception;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class InexistentClockingException extends RuntimeException {
    public InexistentClockingException(LocalTime time) {
        super("No existe ningún fichaje a las  " +
                time.format(DateTimeFormatter.ofPattern("HH:mm")) + " horas");
    }
}
