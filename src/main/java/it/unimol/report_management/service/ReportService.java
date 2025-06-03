package it.unimol.report_management.service;

import org.springframework.http.ResponseEntity;

public interface ReportService {

    // STUDENTE
    ResponseEntity<byte[]> generateStudentActivityPdf(String studentId, String startDate, String endDate, String format);
    ResponseEntity<byte[]> generateStudentGradesPdf(String studentId, String format);
    ResponseEntity<byte[]> generateStudentProgressPdf(String studentId);
    ResponseEntity<byte[]> generateStudentAveragePdf(String studentId);
    ResponseEntity<byte[]> generateStudentCompletionRatePdf(String studentId);
    ResponseEntity<byte[]> generateCourseAveragePdf(String courseId);
    ResponseEntity<byte[]> generateCourseGradeDistributionPdf(String courseId);
    ResponseEntity<byte[]> generateCourseCompletionRatePdf(String courseId);
    ResponseEntity<byte[]> generateTeacherRatingsPdf(String teacherId);
    ResponseEntity<byte[]> generateTeacherAveragePdf(String teacherId);
    ResponseEntity<byte[]> generateTeacherFeedbackPdf(String teacherId);
    ResponseEntity<byte[]> generateStudentPerformanceOverTimePdf(String studentId, String startDate, String endDate, String format);
    ResponseEntity<byte[]> generateCoursePerformanceOverTimePdf(String courseId, String startDate, String endDate, String format);
    ResponseEntity<byte[]> generateTeacherPerformanceOverTimePdf(String teacherId, String startDate, String endDate, String format);
    ResponseEntity<byte[]> generateGlobalSummaryPdf();
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