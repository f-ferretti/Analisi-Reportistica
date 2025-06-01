package it.unimol.report_management.service.impl;

import it.unimol.report_management.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    private byte[] generateDummyPdf(String title) {
        return (title + " PDF content").getBytes();
    }

    private ResponseEntity<?> handleFormat(String title, String format) {
        if ("pdf".equalsIgnoreCase(format)) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + title.replace(" ", "_") + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(generateDummyPdf(title));
        } else {
            Map<String, Object> data = new HashMap<>();
            data.put("report", title);
            data.put("status", "ok");
            return ResponseEntity.ok(data);
        }
    }

    // STUDENTE
    public ResponseEntity<?> getStudentActivity(String studentId, String startDate, String endDate, String format) {
        return handleFormat("Student Activity Report for " + studentId, format);
    }

    public ResponseEntity<?> getStudentGrades(String studentId, String format) {
        return handleFormat("Student Grades Report for " + studentId, format);
    }

    public ResponseEntity<?> getStudentProgress(String studentId) {
        return ResponseEntity.ok(Map.of("studentId", studentId, "progress", "75%"));
    }

    public ResponseEntity<?> getStudentAverage(String studentId) {
        return ResponseEntity.ok(Map.of("studentId", studentId, "average", 28.5));
    }

    public ResponseEntity<?> getStudentCompletionRate(String studentId) {
        return ResponseEntity.ok(Map.of("studentId", studentId, "completionRate", "92%"));
    }

    // CORSO
    public ResponseEntity<?> getCourseAverage(String courseId) {
        return ResponseEntity.ok(Map.of("courseId", courseId, "average", 26.3));
    }

    public ResponseEntity<?> getCourseGradeDistribution(String courseId) {
        return ResponseEntity.ok(Map.of("courseId", courseId, "grades", Map.of("18-21", 10, "22-25", 20, "26-30", 15)));
    }

    public ResponseEntity<?> getCourseCompletionRate(String courseId) {
        return ResponseEntity.ok(Map.of("courseId", courseId, "completionRate", "87%"));
    }

    // DOCENTE
    public ResponseEntity<?> getTeacherRatings(String teacherId) {
        return ResponseEntity.ok(Map.of("teacherId", teacherId, "ratings", 4.6));
    }

    public ResponseEntity<?> getTeacherAverage(String teacherId) {
        return ResponseEntity.ok(Map.of("teacherId", teacherId, "average", 27.1));
    }

    public ResponseEntity<?> getTeacherFeedback(String teacherId) {
        return ResponseEntity.ok(Map.of("teacherId", teacherId, "feedback", "Molto disponibile e chiaro."));
    }

    // AVANZATI
    public ResponseEntity<?> getStudentPerformanceOverTime(String studentId, String startDate, String endDate, String format) {
        return handleFormat("Student Performance Over Time for " + studentId, format);
    }

    public ResponseEntity<?> getCoursePerformanceOverTime(String courseId, String startDate, String endDate, String format) {
        return handleFormat("Course Performance Over Time for " + courseId, format);
    }

    public ResponseEntity<?> getTeacherPerformanceOverTime(String teacherId, String startDate, String endDate, String format) {
        return handleFormat("Teacher Performance Over Time for " + teacherId, format);
    }

    // GLOBALE
    public ResponseEntity<?> getGlobalSummary() {
        return ResponseEntity.ok(Map.of("summary", "Report generale del sistema universitario"));
    }
}