package it.unimol.report_management.exception;

/** Timeout nel chiamare il sistema a monte (stub) (504). */
public class UpstreamTimeoutException extends RuntimeException {
    public UpstreamTimeoutException(String message) { super(message); }
}
