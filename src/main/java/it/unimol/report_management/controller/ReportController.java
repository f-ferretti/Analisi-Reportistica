package it.unimol.report_management.controller;

import it.unimol.report_management.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/report")
@Tag(name = "Report", description = "Gestione report docenti, studenti e corsi con documentazione in italiano")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // === DOCENTI ===

    @Operation(summary = "Ottieni feedback docente",
            description = "Recupera i feedback di un docente in un intervallo di date.")
    @GetMapping("/docenti/{id}/feedback")
    public ResponseEntity<?> ottieniFeedbackDocente(
            @Parameter(description = "ID del docente") @PathVariable("id") int id,
            @Parameter(description = "Data di inizio (YYYY-MM-DD)") @RequestParam String dataInizio,
            @Parameter(description = "Data di fine (YYYY-MM-DD)") @RequestParam String dataFine,
            @Parameter(description = "Formato di output: json o pdf") @RequestParam(defaultValue = "json") String formato
    ) {
        return reportService.getTeacherFeedback(id, dataInizio, dataFine, formato);
    }

    @Operation(summary = "Ottieni valutazioni docente", description = "Recupera le valutazioni di un docente.")
    @GetMapping("/docenti/{id}/valutazioni")
    public ResponseEntity<?> ottieniValutazioniDocente(
            @Parameter(description = "ID del docente") @PathVariable("id") int id,
            @Parameter(description = "Formato di output: json o pdf") @RequestParam(defaultValue = "json") String formato
    ) {
        return reportService.getTeacherRatings(id, formato);
    }

    @Operation(summary = "Ottieni media docente", description = "Recupera la media dei voti per un docente.")
    @GetMapping("/docenti/{id}/media")
    public ResponseEntity<?> ottieniMediaDocente(
            @Parameter(description = "ID del docente") @PathVariable("id") int id,
            @Parameter(description = "Formato di output: json o pdf") @RequestParam(defaultValue = "json") String formato
    ) {
        return reportService.getTeacherAverage(id, formato);
    }

    @Operation(summary = "Ottieni andamento docente", description = "Recupera l'andamento delle performance di un docente in un intervallo di date.")
    @GetMapping("/docenti/{id}/andamento")
    public ResponseEntity<?> ottieniAndamentoDocente(
            @Parameter(description = "ID del docente") @PathVariable("id") int id,
            @Parameter(description = "Data di inizio (YYYY-MM-DD)") @RequestParam String dataInizio,
            @Parameter(description = "Data di fine (YYYY-MM-DD)") @RequestParam String dataFine,
            @Parameter(description = "Formato di output: json o pdf") @RequestParam(defaultValue = "json") String formato
    ) {
        return reportService.getTeacherPerformance(id, dataInizio, dataFine, formato);
    }

    // === STUDENTI ===

    @Operation(summary = "Ottieni voti studente", description = "Recupera i voti di uno studente.")
    @GetMapping("/studenti/{id}/voti")
    public ResponseEntity<?> ottieniVotiStudente(
            @Parameter(description = "ID dello studente") @PathVariable("id") int id,
            @Parameter(description = "Formato di output: json o pdf") @RequestParam(defaultValue = "json") String formato
    ) {
        return reportService.getStudentGrades(id, formato);
    }

    @Operation(summary = "Ottieni progresso studente", description = "Recupera il progresso percentuale di uno studente.")
    @GetMapping("/studenti/{id}/progresso")
    public ResponseEntity<?> ottieniProgressoStudente(
            @Parameter(description = "ID dello studente") @PathVariable("id") int id,
            @Parameter(description = "Formato di output: json o pdf") @RequestParam(defaultValue = "json") String formato
    ) {
        return reportService.getStudentProgress(id, formato);
    }

    @Operation(summary = "Ottieni media studente", description = "Recupera la media dei voti di uno studente.")
    @GetMapping("/studenti/{id}/media")
    public ResponseEntity<?> ottieniMediaStudente(
            @Parameter(description = "ID dello studente") @PathVariable("id") int id,
            @Parameter(description = "Formato di output: json o pdf") @RequestParam(defaultValue = "json") String formato
    ) {
        return reportService.getStudentAverage(id, formato);
    }

    @Operation(summary = "Ottieni completamento studente", description = "Recupera il tasso di completamento di uno studente.")
    @GetMapping("/studenti/{id}/completamento")
    public ResponseEntity<?> ottieniCompletamentoStudente(
            @Parameter(description = "ID dello studente") @PathVariable("id") int id,
            @Parameter(description = "Formato di output: json o pdf") @RequestParam(defaultValue = "json") String formato
    ) {
        return reportService.getStudentCompletionRate(id, formato);
    }

    @Operation(summary = "Ottieni andamento studente", description = "Recupera l'andamento delle performance di uno studente in un intervallo di date.")
    @GetMapping("/studenti/{id}/andamento")
    public ResponseEntity<?> ottieniAndamentoStudente(
            @Parameter(description = "ID dello studente") @PathVariable("id") int id,
            @Parameter(description = "Data di inizio (YYYY-MM-DD)") @RequestParam String dataInizio,
            @Parameter(description = "Data di fine (YYYY-MM-DD)") @RequestParam String dataFine,
            @Parameter(description = "Formato di output: json o pdf") @RequestParam(defaultValue = "json") String formato
    ) {
        return reportService.getStudentPerformance(id, dataInizio, dataFine, formato);
    }

    @Operation(summary = "Ottieni attività studente", description = "Recupera le attività di uno studente in un intervallo di date.")
    @GetMapping("/studenti/{id}/attivita")
    public ResponseEntity<?> ottieniAttivitaStudente(
            @Parameter(description = "ID dello studente") @PathVariable("id") int id,
            @Parameter(description = "Data di inizio (YYYY-MM-DD)") @RequestParam String dataInizio,
            @Parameter(description = "Data di fine (YYYY-MM-DD)") @RequestParam String dataFine,
            @Parameter(description = "Formato di output: json o pdf") @RequestParam(defaultValue = "json") String formato
    ) {
        return reportService.getStudentActivity(id, dataInizio, dataFine, formato);
    }

    // === CORSI ===

    @Operation(summary = "Ottieni media corso", description = "Recupera la media dei voti di un corso.")
    @GetMapping("/corsi/{id}/media")
    public ResponseEntity<?> ottieniMediaCorso(
            @Parameter(description = "ID del corso") @PathVariable("id") int id,
            @Parameter(description = "Formato di output: json o pdf") @RequestParam(defaultValue = "json") String formato
    ) {
        return reportService.getCourseAverage(id, formato);
    }

    @Operation(summary = "Ottieni distribuzione corso", description = "Recupera la distribuzione dei voti di un corso.")
    @GetMapping("/corsi/{id}/distribuzione")
    public ResponseEntity<?> ottieniDistribuzioneCorso(
            @Parameter(description = "ID del corso") @PathVariable("id") int id,
            @Parameter(description = "Formato di output: json o pdf") @RequestParam(defaultValue = "json") String formato
    ) {
        return reportService.getCourseDistribution(id, formato);
    }

    @Operation(summary = "Ottieni completamento corso", description = "Recupera il tasso di completamento di un corso.")
    @GetMapping("/corsi/{id}/completamento")
    public ResponseEntity<?> ottieniCompletamentoCorso(
            @Parameter(description = "ID del corso") @PathVariable("id") int id,
            @Parameter(description = "Formato di output: json o pdf") @RequestParam(defaultValue = "json") String formato
    ) {
        return reportService.getCourseCompletionRate(id, formato);
    }

    @Operation(summary = "Ottieni andamento corso", description = "Recupera l'andamento delle performance di un corso in un intervallo di date.")
    @GetMapping("/corsi/{id}/andamento")
    public ResponseEntity<?> ottieniAndamentoCorso(
            @Parameter(description = "ID del corso") @PathVariable("id") int id,
            @Parameter(description = "Data di inizio (YYYY-MM-DD)") @RequestParam String dataInizio,
            @Parameter(description = "Data di fine (YYYY-MM-DD)") @RequestParam String dataFine,
            @Parameter(description = "Formato di output: json o pdf") @RequestParam(defaultValue = "json") String formato
    ) {
        return reportService.getCoursePerformance(id, dataInizio, dataFine, formato);
    }

    // === RIEPILOGO GLOBALE ===

    @Operation(summary = "Ottieni riepilogo globale", description = "Recupera riepilogo complessivo di docenti, studenti e corsi.")
    @GetMapping("/riepilogo")
    public ResponseEntity<?> ottieniRiepilogoGlobale(
            @Parameter(description = "Formato di output: json o pdf") @RequestParam(defaultValue = "json") String formato
    ) {
        return reportService.getGlobalSummary(formato);
    }
}