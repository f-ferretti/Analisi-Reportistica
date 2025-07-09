package it.unimol.report_management.service;

import org.springframework.http.ResponseEntity;

/**
 * Interfaccia per il servizio che gestisce il forwarding
 * delle richieste al microservizio Python stub e
 * la generazione del report in JSON o PDF.
 */
public interface ReportService {

    // === DOCENTI ===
    ResponseEntity<?> getTeacherFeedback(int teacherId, String startDate, String endDate, String format);
    ResponseEntity<?> getTeacherRatings(int teacherId, String format);
    ResponseEntity<?> getTeacherAverage(int teacherId, String format);
    ResponseEntity<?> getTeacherPerformance(int teacherId, String startDate, String endDate, String format);

    // === STUDENTI ===
    ResponseEntity<?> getStudentGrades(int studentId, String format);
    ResponseEntity<?> getStudentProgress(int studentId, String format);
    ResponseEntity<?> getStudentAverage(int studentId, String format);
    ResponseEntity<?> getStudentCompletionRate(int studentId, String format);
    ResponseEntity<?> getStudentPerformance(int studentId, String startDate, String endDate, String format);
    ResponseEntity<?> getStudentActivity(int studentId, String startDate, String endDate, String format);

    // === CORSI ===
    ResponseEntity<?> getCourseAverage(int courseId, String format);
    ResponseEntity<?> getCourseDistribution(int courseId, String format);
    ResponseEntity<?> getCourseCompletionRate(int courseId, String format);
    ResponseEntity<?> getCoursePerformance(int courseId, String startDate, String endDate, String format);

    // === RIEPILOGO GLOBALE ===
    ResponseEntity<?> getGlobalSummary(String format);
}