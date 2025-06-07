package it.unimol.report_management.controller;

import it.unimol.report_management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // STUDENTE
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/students/{studentId}/activity")
    public ResponseEntity<?> getStudentActivity(
            @PathVariable String studentId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "json") String format) {
        if ("pdf".equalsIgnoreCase(format)) {
            return reportService.generateStudentActivityPdf(studentId, startDate, endDate, format);
        }
        return reportService.getStudentActivity(studentId, startDate, endDate, format);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/students/{studentId}/grades")
    public ResponseEntity<?> getStudentGrades(
            @PathVariable String studentId,
            @RequestParam(defaultValue = "json") String format) {
        if ("pdf".equalsIgnoreCase(format)) {
            return reportService.generateStudentGradesPdf(studentId, format);
        }
        return reportService.getStudentGrades(studentId, format);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/students/{studentId}/progress")
    public ResponseEntity<?> getStudentProgress(
            @PathVariable String studentId,
            @RequestParam(defaultValue = "json") String format) {
        if ("pdf".equalsIgnoreCase(format)) {
            return reportService.generateStudentProgressPdf(studentId);
        }
        return reportService.getStudentProgress(studentId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/students/{studentId}/average")
    public ResponseEntity<?> getStudentAverage(
            @PathVariable String studentId,
            @RequestParam(defaultValue = "json") String format) {
        if ("pdf".equalsIgnoreCase(format)) {
            return reportService.generateStudentAveragePdf(studentId);
        }
        return reportService.getStudentAverage(studentId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/students/{studentId}/completion-rate")
    public ResponseEntity<?> getStudentCompletionRate(
            @PathVariable String studentId,
            @RequestParam(defaultValue = "json") String format) {
        if ("pdf".equalsIgnoreCase(format)) {
            return reportService.generateStudentCompletionRatePdf(studentId);
        }
        return reportService.getStudentCompletionRate(studentId);
    }

    // CORSO
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/courses/{courseId}/average")
    public ResponseEntity<?> getCourseAverage(
            @PathVariable String courseId,
            @RequestParam(defaultValue = "json") String format) {
        if ("pdf".equalsIgnoreCase(format)) {
            return reportService.generateCourseAveragePdf(courseId);
        }
        return reportService.getCourseAverage(courseId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/courses/{courseId}/distribution")
    public ResponseEntity<?> getCourseGradeDistribution(
            @PathVariable String courseId,
            @RequestParam(defaultValue = "json") String format) {
        if ("pdf".equalsIgnoreCase(format)) {
            return reportService.generateCourseGradeDistributionPdf(courseId);
        }
        return reportService.getCourseGradeDistribution(courseId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/courses/{courseId}/completion-rate")
    public ResponseEntity<?> getCourseCompletionRate(
            @PathVariable String courseId,
            @RequestParam(defaultValue = "json") String format) {
        if ("pdf".equalsIgnoreCase(format)) {
            return reportService.generateCourseCompletionRatePdf(courseId);
        }
        return reportService.getCourseCompletionRate(courseId);
    }

    // DOCENTE
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/teachers/{teacherId}/ratings")
    public ResponseEntity<?> getTeacherRatings(
            @PathVariable String teacherId,
            @RequestParam(defaultValue = "json") String format) {
        if ("pdf".equalsIgnoreCase(format)) {
            return reportService.generateTeacherRatingsPdf(teacherId);
        }
        return reportService.getTeacherRatings(teacherId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/teachers/{teacherId}/average")
    public ResponseEntity<?> getTeacherAverage(
            @PathVariable String teacherId,
            @RequestParam(defaultValue = "json") String format) {
        if ("pdf".equalsIgnoreCase(format)) {
            return reportService.generateTeacherAveragePdf(teacherId);
        }
        return reportService.getTeacherAverage(teacherId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/teachers/{teacherId}/feedback")
    public ResponseEntity<?> getTeacherFeedback(
            @PathVariable String teacherId,
            @RequestParam(defaultValue = "json") String format) {
        if ("pdf".equalsIgnoreCase(format)) {
            return reportService.generateTeacherFeedbackPdf(teacherId);
        }
        return reportService.getTeacherFeedback(teacherId);
    }

    // AVANZATI
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/students/{studentId}/performance-over-time")
    public ResponseEntity<?> getStudentPerformanceOverTime(
            @PathVariable String studentId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "json") String format) {
        if ("pdf".equalsIgnoreCase(format)) {
            return reportService.generateStudentPerformanceOverTimePdf(studentId, startDate, endDate, format);
        }
        return reportService.getStudentPerformanceOverTime(studentId, startDate, endDate, format);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/courses/{courseId}/performance-over-time")
    public ResponseEntity<?> getCoursePerformanceOverTime(
            @PathVariable String courseId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "json") String format) {
        if ("pdf".equalsIgnoreCase(format)) {
            return reportService.generateCoursePerformanceOverTimePdf(courseId, startDate, endDate, format);
        }
        return reportService.getCoursePerformanceOverTime(courseId, startDate, endDate, format);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/teachers/{teacherId}/performance-over-time")
    public ResponseEntity<?> getTeacherPerformanceOverTime(
            @PathVariable String teacherId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "json") String format) {
        if ("pdf".equalsIgnoreCase(format)) {
            return reportService.generateTeacherPerformanceOverTimePdf(teacherId, startDate, endDate, format);
        }
        return reportService.getTeacherPerformanceOverTime(teacherId, startDate, endDate, format);
    }

    // GLOBALE - JSON
    @GetMapping(value = "/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getGlobalSummaryJson() {
        Map<String, Object> summary = reportService.getGlobalSummaryMap();
        return ResponseEntity.ok(summary);
    }

    // GLOBALE - PDF
    @GetMapping(value = "/summary", produces = MediaType.APPLICATION_PDF_VALUE, params = "format=pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> getGlobalSummaryPdf() {
        byte[] pdfBytes = reportService.generateGlobalSummaryPdfRaw();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("summary-report.pdf").build());
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}