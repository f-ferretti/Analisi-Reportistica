package it.unimol.report_management.exception;

/** Dati ricevuti dallo stub incompleti o illeggibili (502). */
public class UpstreamBadDataException extends RuntimeException {
    public UpstreamBadDataException(String message) { super(message); }
}
