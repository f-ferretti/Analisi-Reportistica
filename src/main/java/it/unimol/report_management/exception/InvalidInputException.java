package it.unimol.report_management.exception;

/** Input non valido lato client (400). */
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) { super(message); }
}
