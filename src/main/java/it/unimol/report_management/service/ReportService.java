package it.unimol.report_management.service;

import org.springframework.http.ResponseEntity;
import java.util.Map;

public interface ReportService {
    ResponseEntity<byte[]> generateStudentActivityPdf(String studentId, String startDate, String endDate, String format);
    ResponseEntity<?> getStudentActivity(String studentId, String startDate, String endDate, String format);

    ResponseEntity<byte[]> generateStudentGradesPdf(String studentId, String format);
    ResponseEntity<?> getStudentGrades(String studentId, String format);

    ResponseEntity<byte[]> generateStudentProgressPdf(String studentId);
    ResponseEntity<?> getStudentProgress(String studentId);

    ResponseEntity<byte[]> generateStudentAveragePdf(String studentId);
    ResponseEntity<?> getStudentAverage(String studentId);

    ResponseEntity<byte[]> generateStudentCompletionRatePdf(String studentId);
    ResponseEntity<?> getStudentCompletionRate(String studentId);

    ResponseEntity<byte[]> generateCourseAveragePdf(String courseId);
    ResponseEntity<?> getCourseAverage(String courseId);

    ResponseEntity<byte[]> generateCourseGradeDistributionPdf(String courseId);
    ResponseEntity<?> getCourseGradeDistribution(String courseId);

    ResponseEntity<byte[]> generateCourseCompletionRatePdf(String courseId);
    ResponseEntity<?> getCourseCompletionRate(String courseId);

    ResponseEntity<byte[]> generateTeacherRatingsPdf(String teacherId);
    ResponseEntity<?> getTeacherRatings(String teacherId);

    ResponseEntity<byte[]> generateTeacherAveragePdf(String teacherId);
    ResponseEntity<?> getTeacherAverage(String teacherId);

    ResponseEntity<byte[]> generateTeacherFeedbackPdf(String teacherId);
    ResponseEntity<?> getTeacherFeedback(String teacherId);

    ResponseEntity<byte[]> generateStudentPerformanceOverTimePdf(String studentId, String startDate, String endDate, String format);
    ResponseEntity<?> getStudentPerformanceOverTime(String studentId, String startDate, String endDate, String format);

    ResponseEntity<byte[]> generateCoursePerformanceOverTimePdf(String courseId, String startDate, String endDate, String format);
    ResponseEntity<?> getCoursePerformanceOverTime(String courseId, String startDate, String endDate, String format);

    ResponseEntity<byte[]> generateTeacherPerformanceOverTimePdf(String teacherId, String startDate, String endDate, String format);
    ResponseEntity<?> getTeacherPerformanceOverTime(String teacherId, String startDate, String endDate, String format);

    ResponseEntity<byte[]> generateGlobalSummaryPdf();
    ResponseEntity<?> getGlobalSummary();

    // Metodi aggiuntivi richiesti
    Map<String, Object> getGlobalSummaryMap();
    byte[] generateGlobalSummaryPdfRaw();
}