package it.unimol.report_management.controller;

import it.unimol.report_management.dto.student.*;
import it.unimol.report_management.service.StudentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import it.unimol.report_management.util.ChartUtil;
import it.unimol.report_management.util.PdfUtil;
import it.unimol.report_management.web.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Validated
@RequestMapping("/api/v1/students")
@Tag(name = "Studenti", description = "Endpoint studenti (parametri e descrizioni in italiano).")
@PreAuthorize("hasRole('ADMIN')")
public class StudentController {

    private final StudentReportService service;

    public StudentController(StudentReportService service) {
        this.service = service;
    }

    private Integer parseIntOrNull(String s) {
        try { return (s == null || s.isBlank()) ? null : Integer.parseInt(s.trim()); }
        catch (Exception e) { return null; }
    }
    @GetMapping("/{matricola}/exams/passed")
    @Operation(summary = "Esami superati (JSON)")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    public List<ExamResultDTO> passed(
            @PathVariable @Pattern(regexp="\\d{6}", message="La matricola deve essere di esattamente 6 cifre") String matricola) {
        return service.passedExams(matricola);
    }


    @GetMapping("/{matricola}/credits/progress")
    @Operation(summary = "Progresso CFU (JSON)")
    public CreditsProgressDTO credits(
            @PathVariable @Pattern(regexp="\\d{6}", message="La matricola deve essere di esattamente 6 cifre") String matricola) {
        return service.creditsProgress(matricola);
    }

    @GetMapping("/{matricola}/graduation/estimate")
    @Operation(
            summary = "Stima voto di laurea (JSON)",
            description = "Calcolata usando solo i fattori dallo stub. Se omesso, l'anno predefinito è l'anno corrente (**2025**). Se lo specifichi e non esiste, restituisce 404."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Input non valido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Studente/Anno non trovato", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    public GraduationEstimateDTO estimate(
            @PathVariable @Pattern(regexp="\\d{6}", message="La matricola deve essere di esattamente 6 cifre") String matricola,
            @Parameter(description = "Anno accademico. Default: anno corrente (2025). Se fornito ma inesistente → 404.",
                    schema = @Schema(defaultValue = "2025"), example = "2025")
            @RequestParam(value = "annoAccademico", required = false) Integer annoAccademico,
            HttpServletRequest request) {

        if (annoAccademico == null) {
            annoAccademico = parseIntOrNull(request.getParameter("aa"));
            if (annoAccademico == null) annoAccademico = parseIntOrNull(request.getParameter("anno"));
        }
        return service.graduationEstimateFromStub(matricola, annoAccademico);
    }

    @GetMapping("/{matricola}/exams/pending")
    @Operation(summary = "Esami mancanti (JSON)",
            description = "Solo aggregazione/impaginazione. Matricola a 6 cifre.")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Studente non trovato", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    public List<PlanItemDTO> pending(
            @PathVariable @Pattern(regexp="\\d{6}", message="La matricola deve essere di esattamente 6 cifre") String matricola) {
        return service.pendingExams(matricola);
    }

    @Autowired
    private StudentReportService studentReportService;

    @GetMapping(
            value = "/{matricola}/summary.pdf",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    @Operation(
            summary = "Report completo studente (PDF)",
            description = "Genera il report completo dello studente. " +
                    "Se l'anno accademico non è specificato, usa l'anno corrente (2025). " +
                    "Se specificato ma inesistente, restituisce 404. " +
                    "Il PDF include esami superati, crediti, stima voto di laurea e grafici."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File PDF",
                    content = @Content(mediaType = "application/octet-stream",
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "400", description = "Parametri mancanti/non validi",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Studente non trovato",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "Formato dati stub non valido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<?> studentSummaryPdf(
            @PathVariable
            @Pattern(regexp = "^\\d{6}$", message = "Matricola non valida (6 cifre)")
            String matricola,
            @Parameter(description = "Anno accademico (opzionale)", example = "2025")
            @RequestParam(name = "annoAccademico", required = false)
            @Min(2000) Integer annoAccademico
    ) {
        try {
            byte[] pdf = studentReportService.summaryPdf(matricola, annoAccademico);

            // Se il service non ha prodotto nulla → 404 (prima finiva in 401 via Security)
            if (pdf == null || pdf.length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorBody(404,
                                "Studente " + matricola + " non trovato",
                                "/api/v1/students/" + matricola + "/summary.pdf",
                                "ResourceNotFoundException"));
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("student_summary_" + matricola +
                            (annoAccademico != null ? "_" + annoAccademico : "") + ".pdf")
                    .build());
            headers.setCacheControl("no-store");
            headers.add("Access-Control-Expose-Headers", "Content-Disposition");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

        } catch (org.springframework.web.server.ResponseStatusException rse) {
            return ResponseEntity.status(rse.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorBody(rse.getStatusCode().value(), rse.getReason(),
                            "/api/v1/students/" + matricola + "/summary.pdf", "ResponseStatusException"));

        } catch (it.unimol.report_management.exception.ResourceNotFoundException rnfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorBody(404, rnfe.getMessage(),
                            "/api/v1/students/" + matricola + "/summary.pdf", "ResourceNotFoundException"));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorBody(500, "Errore interno",
                            "/api/v1/students/" + matricola + "/summary.pdf", ex.getClass().getSimpleName()));
        }
    }

    /** helper locale per corpo errore, evita che la catena Security trasformi tutto in 401 */
    private Map<String, Object> errorBody(int status, String message, String path, String cause) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("timestamp", java.time.OffsetDateTime.now());
        m.put("status", status);
        m.put("error", HttpStatus.valueOf(status).getReasonPhrase());
        m.put("path", path);
        m.put("message", (message == null || message.isBlank())
                ? HttpStatus.valueOf(status).getReasonPhrase() : message);
        Map<String, String> details = new LinkedHashMap<>();
        details.put("cause", cause);
        m.put("details", details);
        m.put("correlationId", null);
        return m;
    }
}
