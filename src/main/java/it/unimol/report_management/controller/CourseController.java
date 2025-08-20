package it.unimol.report_management.controller;

import it.unimol.report_management.dto.course.EnrollmentDTO;
import it.unimol.report_management.dto.student.ExamResultDTO;
import it.unimol.report_management.service.CourseReportService;
import it.unimol.report_management.util.PdfUtil;
import it.unimol.report_management.web.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.awt.image.BufferedImage;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/v1/courses")
@Tag(name = "Corsi", description = "Endpoint corsi.")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class CourseController {

    private final CourseReportService service;

    public CourseController(CourseReportService service) {
        this.service = service;
    }

    private Integer parseIntOrNull(String s) {
        try { return (s == null || s.isBlank()) ? null : Integer.parseInt(s.trim()); }
        catch (Exception e) { return null; }
    }

    private Integer resolveAnnoAccademico(HttpServletRequest request, Integer annoAccademico) {
        Integer aa = annoAccademico;
        if (aa == null) aa = parseIntOrNull(request.getParameter("aa"));
        if (aa == null) aa = parseIntOrNull(request.getParameter("anno"));
        return aa;
    }

    // -------------------- Iscritti per anno (aggregato) --------------------
    @GetMapping("/{courseCode}/enrollments")
    @Operation(
            summary = "Iscritti al corso (JSON)"

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Corso non trovato",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    public List<Map<String, Integer>> enrollments(
            @PathVariable
            @Pattern(regexp = "^[A-Za-z0-9_-]{2,32}$", message = "Codice corso non valido")
            String courseCode,
            HttpServletRequest request
    ) {
        return service.enrollmentsByYear(courseCode);
    }

    // -------------------- Voti del corso (JSON) --------------------
    @GetMapping("/{courseCode}/grades")
    @Operation(
            summary = "Voti del corso (JSON)",
            description = "Restituisce i voti degli studenti per il corso e l'anno accademico indicato. " +
                    "Se non specificato, l'anno accademico viene risolto dalla richiesta (parametro 'annoAccademico'). " +
                    "Se non presente, si assume l'anno accademico corrente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Corso/anno non trovato",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    public List<ExamResultDTO> courseGrades(
            @PathVariable
            @Pattern(regexp = "^[A-Za-z0-9_-]{2,32}$", message = "Codice corso non valido")
            String courseCode,
            @Parameter(description = "Anno accademico (opzionale)", example = "2025")
            @RequestParam(value = "annoAccademico", required = false)
            @Min(2000) Integer annoAccademico,
            HttpServletRequest request
    ) {
        Integer aa = resolveAnnoAccademico(request, annoAccademico);
        return service.courseGrades(courseCode, aa);
    }

    // -------------------- Distribuzione voti corso (PDF → download diretto) --------------------
    @GetMapping(
            value = "/{courseCode}/grades/distribution.pdf",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    @Operation(
            summary = "Distribuzione voti corso (PDF)",
            description = "Istogramma dei voti per il corso e l'anno accademico indicato."
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
            @ApiResponse(responseCode = "404", description = "Corso/anno non trovato",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "Formato dati stub non valido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<?> courseGradesDistributionPdf(
            @PathVariable
            @Pattern(regexp = "^[A-Za-z0-9_-]{2,32}$", message = "Codice corso non valido")
            String courseCode,
            @Parameter(description = "Anno accademico (obbligatorio)", example = "2025")
            @RequestParam(name = "annoAccademico", required = true)
            @Min(2000) Integer annoAccademico,
            HttpServletRequest request
    ) {
        Integer aa = resolveAnnoAccademico(request, annoAccademico);

        try {
            BufferedImage chart = service.courseGradesDistributionChart(courseCode, aa);
            byte[] pdf = PdfUtil.imageToPdf(
                    chart,
                    "Distribuzione voti - " + courseCode + " (" + aa + ")",
                    null
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                    ContentDisposition.attachment()
                            .filename("course_distribution_" + courseCode + "_" + aa + ".pdf")
                            .build()
            );
            headers.setCacheControl("no-store");
            headers.add("Access-Control-Expose-Headers", "Content-Disposition");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

        } catch (ResponseStatusException rse) {
            return ResponseEntity
                    .status(rse.getStatusCode().value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorBody(rse.getStatusCode().value(), rse.getReason(), request.getRequestURI(), "ResponseStatusException"));

        } catch (it.unimol.report_management.exception.ResourceNotFoundException rnfe) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorBody(404, rnfe.getMessage(), request.getRequestURI(), "ResourceNotFoundException"));

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorBody(500, "Errore interno", request.getRequestURI(), ex.getClass().getSimpleName()));
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