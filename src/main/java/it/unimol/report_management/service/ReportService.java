package it.unimol.report_management.service;

import org.springframework.http.ResponseEntity;

public interface ReportService {

    // STUDENTE
    ResponseEntity<?> getStudentActivity(String studentId, String startDate, String endDate, String format);
    ResponseEntity<?> getStudentGrades(String studentId, String format);
    ResponseEntity<?> getStudentProgress(String studentId);
    ResponseEntity<?> getStudentAverage(String studentId);
    ResponseEntity<?> getStudentCompletionRate(String studentId);

    // CORSO
    ResponseEntity<?> getCourseAverage(String courseId);
    ResponseEntity<?> getCourseGradeDistribution(String courseId);
    ResponseEntity<?> getCourseCompletionRate(String courseId);

    // DOCENTE
    ResponseEntity<?> getTeacherRatings(String teacherId);
    ResponseEntity<?> getTeacherAverage(String teacherId);
    ResponseEntity<?> getTeacherFeedback(String teacherId);

    // AVANZATI
    ResponseEntity<?> getStudentPerformanceOverTime(String studentId, String startDate, String endDate, String format);
    ResponseEntity<?> getCoursePerformanceOverTime(String courseId, String startDate, String endDate, String format);
    ResponseEntity<?> getTeacherPerformanceOverTime(String teacherId, String startDate, String endDate, String format);

    // REPORT COMPLESSIVO
    ResponseEntity<?> getGlobalSummary();
}