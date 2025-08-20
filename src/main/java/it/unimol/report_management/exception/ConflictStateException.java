package it.unimol.report_management.exception;

/** Stato incoerente rispetto ai dati (409). */
public class ConflictStateException extends RuntimeException {
    public ConflictStateException(String message) { super(message); }
}
