package dev.jugapi.workcount.domain.exception;

import java.time.YearMonth;

public class TemplateNotFoundException extends RuntimeException {
    public TemplateNotFoundException(YearMonth month) {
        super("No se ha encontrado una plantilla para el mes " + month);
    }
}
