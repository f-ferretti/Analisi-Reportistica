package it.unimol.report_management.web;

import it.unimol.report_management.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler {

    // 404 per la tua eccezione custom (top-level)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, "ResourceNotFoundException");
    }

    // Rispetta lo status nativo di ResponseStatusException (400/404/422 ecc.)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleRSE(ResponseStatusException ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.valueOf(ex.getStatusCode().value());
        return build(st, ex.getReason(), req, "ResponseStatusException");
    }

    // 400 per problemi di validazione/parametri
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<Map<String, Object>> handleBadRequest(Exception ex, HttpServletRequest req) {
        String msg = "Richiesta non valida";
        if (ex instanceof MethodArgumentNotValidException manv) {
            msg = manv.getBindingResult().getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.joining("; "));
        } else if (ex instanceof ConstraintViolationException cve) {
            msg = cve.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining("; "));
        } else if (ex instanceof MissingServletRequestParameterException mrp) {
            msg = "Parametro mancante: " + mrp.getParameterName();
        } else if (ex instanceof MethodArgumentTypeMismatchException mat) {
            msg = "Parametro non valido: " + mat.getName();
        } else if (ex instanceof HttpMessageNotReadableException) {
            msg = "Body non leggibile";
        }
        return build(HttpStatus.BAD_REQUEST, msg, req, ex.getClass().getSimpleName());
    }

    // 401/403 sicurezza
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuth(AuthenticationException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Token mancante o non valido", req, "AuthenticationException");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleDenied(AccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "Accesso negato", req, "AccessDeniedException");
    }

    // Catch-all: se nella catena delle cause c'è ResourceNotFoundException -> 404, altrimenti 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAny(Exception ex, HttpServletRequest req) {
        Throwable t = ex;
        while (t != null) {
            if (t instanceof ResourceNotFoundException rnfe) {
                return build(HttpStatus.NOT_FOUND, rnfe.getMessage(), req, "ResourceNotFoundException");
            }
            t = t.getCause();
        }
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Errore interno", req, ex.getClass().getSimpleName());
    }

    // Builder JSON col tuo formato
    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message, HttpServletRequest req, String cause) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("path", req.getRequestURI());
        body.put("message", (message == null || message.isBlank()) ? status.getReasonPhrase() : message);

        Map<String, String> details = new LinkedHashMap<>();
        details.put("cause", cause);
        body.put("details", details);

        body.put("correlationId", null);
        return ResponseEntity.status(status).body(body);
    }
}