package it.unimol.report_management.controller;

import it.unimol.report_management.client.StubClient;
import it.unimol.report_management.dto.teacher.TeacherConsistencyDTO;
import it.unimol.report_management.service.TeacherReportService;
import it.unimol.report_management.service.TeacherReportServiceImpl;
import it.unimol.report_management.web.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/v1/teachers")
@Tag(name = "Docenti", description = "Endpoint docenti.")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class TeacherController {

    @Autowired(required = false)
    private TeacherReportService teacherService;

    @Autowired
    private StubClient stubClient;

    @PostConstruct
    private void initTeacherServiceFallback() {
        if (this.teacherService == null) {
            this.teacherService = new TeacherReportService() {
                @Override
                public byte[] gradesDistributionPdf(String teacherId, Integer aa) {
                    return new byte[0];
                }

                @Override
                public TeacherConsistencyDTO consistency(String teacherId, String courseCode, Integer from, Integer to) {
                    return null;
                }
            };
        }
    }

    // -------------------- Distribuzione voti docente (PDF → download diretto) --------------------
    @GetMapping(
            value = "/{docenteId}/grades/distribution.pdf",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    @Operation(
            summary = "Distribuzione voti (PDF) per docente e anno accademico",
            description = "Aggrega i voti assegnati dal docente nell'anno indicato."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "File PDF",
                    content = @Content(mediaType = "application/octet-stream",
                            schema = @Schema(type = "string", format = "binary"))
            ),
            @ApiResponse(responseCode = "400", description = "Parametri mancanti/non validi",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Docente/anno non trovato",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "Formato dati stub non valido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<?> gradesDistributionPdf(
            @PathVariable
            @Pattern(regexp = "^DOC\\d{3}$", message = "ID docente non valido (es. DOC123)")
            String docenteId,
            @Parameter(description = "Anno accademico (obbligatorio)", example = "2025")
            @RequestParam(name = "annoAccademico", required = true)
            @Min(2000) Integer annoAccademico
    ) {
        try {
            byte[] pdf = teacherService.gradesDistributionPdf(docenteId, annoAccademico);

            // Se il service non ha prodotto nulla, ritorna 404)
            if (pdf == null || pdf.length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorBody(404, "Nessun dato disponibile per docente " + docenteId + " (" + annoAccademico + ")",
                                "/api/v1/teachers/" + docenteId + "/grades/distribution.pdf", "NotFound"));
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                    ContentDisposition.attachment()
                            .filename("teacher_distribution_" + docenteId + "_" + annoAccademico + ".pdf")
                            .build()
            );
            headers.setCacheControl("no-store");
            headers.add("Access-Control-Expose-Headers", "Content-Disposition");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

        } catch (ResponseStatusException rse) {
            return ResponseEntity
                    .status(rse.getStatusCode().value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorBody(rse.getStatusCode().value(), rse.getReason(),
                            "/api/v1/teachers/" + docenteId + "/grades/distribution.pdf", "ResponseStatusException"));

        } catch (it.unimol.report_management.exception.ResourceNotFoundException rnfe) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorBody(404, rnfe.getMessage(),
                            "/api/v1/teachers/" + docenteId + "/grades/distribution.pdf", "ResourceNotFoundException"));

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorBody(500, "Errore interno",
                            "/api/v1/teachers/" + docenteId + "/grades/distribution.pdf", ex.getClass().getSimpleName()));
        }
    }

    // -------------------- Consistenza anno-su-anno (JSON) --------------------
    @GetMapping("/{docenteId}/consistency")
    @Operation(
            summary = "Consistenza anno‑su‑anno per docente/corso",
            description = "Richiede courseCode e un intervallo di anni [from,to]. Verifica che il docente tenga il corso nell'intervallo e confronta i voti assegnati."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeacherConsistencyDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parametri mancanti/non validi",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Nessun dato disponibile per docente/corso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<?> consistency(
            @PathVariable
            @Pattern(regexp = "^DOC\\d{3}$", message = "ID docente non valido (es. DOC123)")
            String docenteId,
            @Parameter(description = "Codice corso (obbligatorio)", example = "ALG201")
            @RequestParam(name = "courseCode", required = true)
            @Pattern(regexp = "^[A-Za-z0-9_-]{2,32}$", message = "Codice corso non valido")
            String courseCode,
            @Parameter(description = "Anno iniziale (opzionale). Se non indicato usa l'anno corrente - 4. (Es. 2025 - 4 = 2021)")
            @RequestParam(name = "from", required = false)
            @Min(2000) Integer from,
            @Parameter(description = "Anno finale (opzionale). Default: anno corrente.")
            @RequestParam(name = "to", required = false)
            @Min(2000) Integer to
    ) {
        try {
            TeacherConsistencyDTO dto = teacherService.consistency(docenteId, courseCode, from, to);
            return ResponseEntity.ok(dto);

        } catch (ResponseStatusException rse) {
            return ResponseEntity
                    .status(rse.getStatusCode().value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorBody(rse.getStatusCode().value(), rse.getReason(),
                            "/api/v1/teachers/" + docenteId + "/consistency", "ResponseStatusException"));

        } catch (it.unimol.report_management.exception.ResourceNotFoundException rnfe) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorBody(404, rnfe.getMessage(),
                            "/api/v1/teachers/" + docenteId + "/consistency", "ResourceNotFoundException"));

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorBody(500, "Errore interno",
                            "/api/v1/teachers/" + docenteId + "/consistency", ex.getClass().getSimpleName()));
        }
    }

    // -------- helper locale per corpo errore (evita conversioni a 401) --------
    private Map<String, Object> errorBody(int status, String message, String path, String cause) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("timestamp", OffsetDateTime.now());
        m.put("status", status);
        m.put("error", HttpStatus.valueOf(status).getReasonPhrase());
        m.put("path", path);
        m.put("message", (message == null || message.isBlank()) ? HttpStatus.valueOf(status).getReasonPhrase() : message);
        Map<String, String> details = new LinkedHashMap<>();
        details.put("cause", cause);
        m.put("details", details);
        m.put("correlationId", null);
        return m;
    }
}