package it.unimol.report_management.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO standard per tutte le risposte di errore.
 */
@Schema(name = "ErrorResponse", description = "Formato uniforme degli errori API")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @Schema(description = "Timestamp UTC ISO-8601", example = "2025-08-10T18:54:21Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime timestamp;

    @Schema(description = "Path della richiesta", example = "/api/v1/students/123/exams/passed")
    private String path;

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "Codice errore applicativo")
    private ApiErrorCode error;

    @Schema(description = "Messaggio sintetico per l'utente", example = "Studente 123 non trovato")
    private String message;

    @ArraySchema(arraySchema = @Schema(description = "Dettagli dei campi non validi"))
    private List<FieldIssue> details;

    @Schema(description = "Correlation id per tracciare la richiesta", example = "3a7b7c23-2f7a-4f3a-8b59-1f9c1e7a9d91")
    private String correlationId;

    public ErrorResponse() {
        this.timestamp = OffsetDateTime.now(ZoneOffset.UTC);
        this.details = new ArrayList<>();
    }

    public static ErrorResponse of(String path, int status, ApiErrorCode code, String message, String correlationId) {
        ErrorResponse r = new ErrorResponse();
        r.setPath(path);
        r.setStatus(status);
        r.setError(code);
        r.setMessage(message);
        r.setCorrelationId(correlationId);
        return r;
    }

    public void addDetail(String field, String issue) {
        if (this.details == null) this.details = new ArrayList<>();
        this.details.add(new FieldIssue(field, issue));
    }

    // --- getters & setters ---

    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public ApiErrorCode getError() { return error; }
    public void setError(ApiErrorCode error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<FieldIssue> getDetails() { return details; }
    public void setDetails(List<FieldIssue> details) { this.details = details; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    // --- nested DTO ---

    @Schema(name = "FieldIssue", description = "Violazione su un campo")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FieldIssue {
        @Schema(description = "Nome del campo", example = "matricola")
        private String field;
        @Schema(description = "Problema riscontrato", example = "formato non valido; attesi 6-10 numeri")
        private String issue;

        public FieldIssue() {}
        public FieldIssue(String field, String issue) {
            this.field = field;
            this.issue = issue;
        }
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        public String getIssue() { return issue; }
        public void setIssue(String issue) { this.issue = issue; }
    }
}
