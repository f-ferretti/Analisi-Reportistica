package it.unimol.report_management.error;

/**
 * Codici di errore uniformi per tutte le risposte JSON di errore.
 */
public enum ApiErrorCode {
    VALIDATION_ERROR,
    NOT_FOUND,
    CONFLICT,
    UNAUTHORIZED,
    FORBIDDEN,
    METHOD_NOT_ALLOWED,
    UNSUPPORTED_MEDIA_TYPE,
    NOT_ACCEPTABLE,
    UPSTREAM_TIMEOUT,
    UPSTREAM_BAD_DATA,
    INTERNAL_ERROR
}
