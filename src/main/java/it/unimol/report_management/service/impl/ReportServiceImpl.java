// INIZIO FILE: ReportServiceImpl.java
package it.unimol.report_management.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import it.unimol.report_management.client.*;
import it.unimol.report_management.service.ReportService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    private final UtentiClient utentiClient;
    private final EsamiClient esamiClient;
    private final CorsiClient corsiClient;
    private final CompitiClient compitiClient;
    private final PresenzeClient presenzeClient;
    private final FeedbackClient feedbackClient;

    public ReportServiceImpl(UtentiClient utentiClient, EsamiClient esamiClient, CorsiClient corsiClient,
                             CompitiClient compitiClient, PresenzeClient presenzeClient, FeedbackClient feedbackClient) {
        this.utentiClient = utentiClient;
        this.esamiClient = esamiClient;
        this.corsiClient = corsiClient;
        this.compitiClient = compitiClient;
        this.presenzeClient = presenzeClient;
        this.feedbackClient = feedbackClient;
    }

    // ================================
    // ========== METODI PDF ==========
    // ================================

    @Override
    public ResponseEntity<byte[]> generateStudentActivityPdf(String studentId, String startDate, String endDate, String format) {
        return generateBasicPdf("Attività Studente", studentId, startDate, endDate, new String[][]{
                {"Lezione: Reti", "2025-03-15", "Presente"},
                {"Esame: Basi di Dati", "2025-03-18", "Superato (28)"},
                {"Compito: Microservizi", "2025-03-22", "Consegnato"}
        });
    }

    @Override
    public ResponseEntity<byte[]> generateStudentGradesPdf(String studentId, String format) {
        return generateBasicPdf("Voti Studente", studentId, null, null, new String[][]{
                {"Basi di Dati", "2025-02-10", "30L"},
                {"Sistemi Operativi", "2025-03-01", "28"},
                {"Reti di Calcolatori", "2025-04-12", "27"}
        });
    }

    @Override
    public ResponseEntity<byte[]> generateStudentProgressPdf(String studentId) {
        return generateBasicPdf("Progresso Studente", studentId, null, null, new String[][]{
                {"Settimana 1", "2025-02-01", "80%"},
                {"Settimana 2", "2025-02-08", "85%"},
                {"Settimana 3", "2025-02-15", "90%"}
        });
    }

    @Override
    public ResponseEntity<byte[]> generateStudentAveragePdf(String studentId) {
        return generateBasicPdf("Media Voti Studente", studentId, null, null, new String[][]{
                {"Totale Esami", "—", "5"},
                {"Media Aritmetica", "—", "27.8"},
                {"Media Ponderata", "—", "28.1"}
        });
    }

    @Override
    public ResponseEntity<byte[]> generateStudentCompletionRatePdf(String studentId) {
        return generateBasicPdf("Completamento Studente", studentId, null, null, new String[][]{
                {"Compiti Totali", "—", "10"},
                {"Compiti Completati", "—", "9"},
                {"Completamento", "—", "90%"}
        });
    }

    @Override
    public ResponseEntity<byte[]> generateCourseAveragePdf(String courseId) {
        return generateBasicPdf("Media Corso", courseId, null, null, new String[][]{
                {"Esami Totali", "—", "30"},
                {"Media Corso", "—", "26.4"}
        });
    }

    @Override
    public ResponseEntity<byte[]> generateCourseGradeDistributionPdf(String courseId) {
        return generateBasicPdf("Distribuzione Voti Corso", courseId, null, null, new String[][]{
                {"18–21", "10", "33%"},
                {"22–25", "12", "40%"},
                {"26–30", "8", "27%"}
        });
    }

    @Override
    public ResponseEntity<byte[]> generateCourseCompletionRatePdf(String courseId) {
        return generateBasicPdf("Completamento Corso", courseId, null, null, new String[][]{
                {"Studenti Iscritti", "—", "50"},
                {"Studenti Completato", "—", "45"},
                {"Tasso", "—", "90%"}
        });
    }

    @Override
    public ResponseEntity<byte[]> generateTeacherRatingsPdf(String teacherId) {
        return generateBasicPdf("Valutazioni Docente", teacherId, null, null, new String[][]{
                {"Lezione 1", "2025-03-10", "4.5/5"},
                {"Lezione 2", "2025-03-17", "4.7/5"}
        });
    }

    @Override
    public ResponseEntity<byte[]> generateTeacherAveragePdf(String teacherId) {
        return generateBasicPdf("Media Docente", teacherId, null, null, new String[][]{
                {"Totale Valutazioni", "—", "60"},
                {"Media", "—", "4.6/5"}
        });
    }

    @Override
    public ResponseEntity<byte[]> generateTeacherFeedbackPdf(String teacherId) {
        return generateBasicPdf("Feedback Docente", teacherId, null, null, new String[][]{
                {"Studente 1", "2025-03-21", "Molto chiaro"},
                {"Studente 2", "2025-03-25", "Coinvolgente e preciso"}
        });
    }

    @Override
    public ResponseEntity<byte[]> generateStudentPerformanceOverTimePdf(String studentId, String startDate, String endDate, String format) {
        return generateBasicPdf("Performance Studente nel Tempo", studentId, startDate, endDate, new String[][]{
                {"Gennaio", "—", "Media 27"},
                {"Febbraio", "—", "Media 28"},
                {"Marzo", "—", "Media 29"}
        });
    }

    @Override
    public ResponseEntity<byte[]> generateCoursePerformanceOverTimePdf(String courseId, String startDate, String endDate, String format) {
        return generateBasicPdf("Performance Corso nel Tempo", courseId, startDate, endDate, new String[][]{
                {"2024", "—", "Media 25"},
                {"2025", "—", "Media 27"}
        });
    }

    @Override
    public ResponseEntity<byte[]> generateTeacherPerformanceOverTimePdf(String teacherId, String startDate, String endDate, String format) {
        return generateBasicPdf("Performance Docente nel Tempo", teacherId, startDate, endDate, new String[][]{
                {"2024", "—", "Valutazione 4.3"},
                {"2025", "—", "Valutazione 4.7"}
        });
    }

    @Override
    public ResponseEntity<byte[]> generateGlobalSummaryPdf() {
        return generateBasicPdf("Report Sommario Globale", "Sistema", null, null, new String[][]{
                {"Studenti Attivi", "—", "120"},
                {"Media Globale", "—", "26.8"},
                {"Feedback Positivi", "—", "92%"}
        });
    }

    private ResponseEntity<byte[]> generateBasicPdf(String title, String id, String startDate, String endDate, String[][] rows) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font cellFont = new Font(Font.HELVETICA, 11);

            Paragraph titleP = new Paragraph("Report: " + title, titleFont);
            titleP.setAlignment(Element.ALIGN_CENTER);
            titleP.setSpacingAfter(20f);
            document.add(titleP);

            document.add(new Paragraph("ID: " + id));
            if (startDate != null && endDate != null) {
                document.add(new Paragraph("Periodo: " + startDate + " → " + endDate));
            }
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 2, 2});
            table.addCell(new PdfPCell(new Phrase("Descrizione", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Data", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Dettagli", headerFont)));

            for (String[] row : rows) {
                for (String cell : row) {
                    table.addCell(new PdfPCell(new Phrase(cell, cellFont)));
                }
            }

            document.add(table);
            document.close();

            byte[] pdfBytes = baos.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "report_" + title.replace(" ", "_").toLowerCase() + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ================================
    // ========== METODI JSON =========
    // ================================

    @Override
    public ResponseEntity<?> getStudentActivity(String studentId, String startDate, String endDate, String format) {
        return ResponseEntity.ok(Map.of("mock", true, "description", "Student Activity Report for " + studentId));
    }

    @Override
    public ResponseEntity<?> getStudentGrades(String studentId, String format) {
        return ResponseEntity.ok(Map.of("mock", true, "description", "Student Grades Report for " + studentId));
    }

    @Override
    public ResponseEntity<?> getStudentProgress(String studentId) {
        return ResponseEntity.ok(Map.of("mock", true, "description", "Student Progress Report for " + studentId));
    }

    @Override
    public ResponseEntity<?> getStudentAverage(String studentId) {
        return ResponseEntity.ok(Map.of("mock", true, "description", "Student Average Report for " + studentId));
    }

    @Override
    public ResponseEntity<?> getStudentCompletionRate(String studentId) {
        return ResponseEntity.ok(Map.of("mock", true, "description", "Student Completion Rate Report for " + studentId));
    }

    @Override
    public ResponseEntity<?> getCourseAverage(String courseId) {
        return ResponseEntity.ok(Map.of("mock", true, "description", "Course Average Report for " + courseId));
    }

    @Override
    public ResponseEntity<?> getCourseGradeDistribution(String courseId) {
        return ResponseEntity.ok(Map.of("mock", true, "description", "Course Grade Distribution Report for " + courseId));
    }

    @Override
    public ResponseEntity<?> getCourseCompletionRate(String courseId) {
        return ResponseEntity.ok(Map.of("mock", true, "description", "Course Completion Rate Report for " + courseId));
    }

    @Override
    public ResponseEntity<?> getTeacherRatings(String teacherId) {
        return ResponseEntity.ok(Map.of("mock", true, "description", "Teacher Ratings Report for " + teacherId));
    }

    @Override
    public ResponseEntity<?> getTeacherAverage(String teacherId) {
        return ResponseEntity.ok(Map.of("mock", true, "description", "Teacher Average Report for " + teacherId));
    }

    @Override
    public ResponseEntity<?> getTeacherFeedback(String teacherId) {
        return ResponseEntity.ok(Map.of("mock", true, "description", "Teacher Feedback Report for " + teacherId));
    }

    @Override
    public ResponseEntity<?> getStudentPerformanceOverTime(String studentId, String startDate, String endDate, String format) {
        return ResponseEntity.ok(Map.of("mock", true, "description", "Student Performance Over Time for " + studentId));
    }

    @Override
    public ResponseEntity<?> getCoursePerformanceOverTime(String courseId, String startDate, String endDate, String format) {
        return ResponseEntity.ok(Map.of("mock", true, "description", "Course Performance Over Time for " + courseId));
    }

    @Override
    public ResponseEntity<?> getTeacherPerformanceOverTime(String teacherId, String startDate, String endDate, String format) {
        return ResponseEntity.ok(Map.of("mock", true, "description", "Teacher Performance Over Time for " + teacherId));
    }

    @Override
    public ResponseEntity<?> getGlobalSummary() {
        return ResponseEntity.ok(Map.of("mock", true, "description", "Global Summary Report"));
    }
}