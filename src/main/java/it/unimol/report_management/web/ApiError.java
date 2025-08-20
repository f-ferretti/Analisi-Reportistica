package it.unimol.report_management.web;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.Map;

@Schema(description = "Errore standard dell'API")
public class ApiError {

    @Schema(example = "2025-08-11T17:53:00+02:00")
    private OffsetDateTime timestamp;

    @Schema(example = "404")
    private int status;

    @Schema(example = "Not Found")
    private String error;

    @Schema(example = "/api/v1/courses/ST101/attendance/percentages.pdf")
    private String path;

    @Schema(example = "Corso ST101 non trovato")
    private String message;

    @Schema(description = "Dettagli specifici dell'errore")
    private Map<String, Object> details;

    @Schema(example = "7f0e2d4b1bef47c4b4f0e4a0d2a3c1f2")
    private String correlationId;

    public ApiError() {}

    public ApiError(OffsetDateTime timestamp, int status, String error, String path, String message, Map<String,Object> details, String correlationId) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.path = path;
        this.message = message;
        this.details = details;
        this.correlationId = correlationId;
    }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
}
