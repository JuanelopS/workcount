package dev.jugapi.workcount.domain.exception;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class AlreadyRegisteredDayException extends RuntimeException{
    public AlreadyRegisteredDayException(LocalDate date) {
        System.out.println("Ya existe un registro para el día " +
                date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    }
}
