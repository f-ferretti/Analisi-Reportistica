package it.unimol.report_management.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    /* 1) Preserva lo status delle ResponseStatusException (404, 400, ...) */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatus(ResponseStatusException ex, HttpServletRequest req) {
        return build(ex.getStatusCode(), ex.getReason(), req, Map.of("cause", ex.getClass().getSimpleName()));
    }

    /* 2) Bad Request per errori di input/validazione */
    @ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<Object> handleBadRequest(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req, Map.of("cause", ex.getClass().getSimpleName()));
    }

    /* 3) Non implementato */
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<Object> handleNotImplemented(UnsupportedOperationException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_IMPLEMENTED, ex.getMessage(), req, Map.of("cause", ex.getClass().getSimpleName()));
    }

    /* 4) Catch-all → 500 (senza schiacciare le ResponseStatusException) */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAny(Exception ex, HttpServletRequest req) {
        if (ex instanceof ResponseStatusException rse) {
            return handleResponseStatus(rse, req);
        }
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Errore interno", req, Map.of("cause", ex.getClass().getSimpleName()));
    }

    /* --- builder body uniforme --- */
    private ResponseEntity<Object> build(HttpStatusCode status, String message, HttpServletRequest req, Map<String, Object> details) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", HttpStatus.valueOf(status.value()).getReasonPhrase());
        body.put("path", req.getRequestURI());
        if (message != null && !message.isBlank()) body.put("message", message);
        if (details != null && !details.isEmpty()) body.put("details", details);
        body.put("correlationId", req.getHeader("X-Correlation-Id"));
        return ResponseEntity.status(status).body(body);
    }
}