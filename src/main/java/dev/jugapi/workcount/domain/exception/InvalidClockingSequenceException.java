package dev.jugapi.workcount.domain.exception;

import dev.jugapi.workcount.domain.model.Clocking;

public class InvalidClockingSequenceException extends RuntimeException {
    public InvalidClockingSequenceException(Clocking clocking) {
        super("No puede realizarse este tipo de fichaje " + clocking.type());
    }
}
