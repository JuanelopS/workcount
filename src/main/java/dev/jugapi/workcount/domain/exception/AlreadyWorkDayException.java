package dev.jugapi.workcount.domain.exception;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AlreadyWorkDayException extends RuntimeException{
    public AlreadyWorkDayException(LocalDate date) {
        super("Ya existe un registro para el día " +
                date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    }
}
