package it.unimol.report_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import it.unimol.report_management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // STUDENTE
    @GetMapping("/students/{studentId}/activity")
    public ResponseEntity<?> getStudentActivity(@PathVariable String studentId,
                                                @RequestParam(required = false) String startDate,
                                                @RequestParam(required = false) String endDate,
                                                @RequestParam(defaultValue = "json") String format) {
        return reportService.getStudentActivity(studentId, startDate, endDate, format);
    }

    @GetMapping("/students/{studentId}/grades")
    public ResponseEntity<?> getStudentGrades(@PathVariable String studentId,
                                              @RequestParam(defaultValue = "json") String format) {
        return reportService.getStudentGrades(studentId, format);
    }

    @GetMapping("/students/{studentId}/progress")
    public ResponseEntity<?> getStudentProgress(@PathVariable String studentId) {
        return reportService.getStudentProgress(studentId);
    }

    @GetMapping("/students/{studentId}/average")
    public ResponseEntity<?> getStudentAverage(@PathVariable String studentId) {
        return reportService.getStudentAverage(studentId);
    }

    @GetMapping("/students/{studentId}/completion-rate")
    public ResponseEntity<?> getStudentCompletionRate(@PathVariable String studentId) {
        return reportService.getStudentCompletionRate(studentId);
    }

    // CORSO
    @GetMapping("/courses/{courseId}/average")
    public ResponseEntity<?> getCourseAverage(@PathVariable String courseId) {
        return reportService.getCourseAverage(courseId);
    }

    @GetMapping("/courses/{courseId}/distribution")
    public ResponseEntity<?> getCourseGradeDistribution(@PathVariable String courseId) {
        return reportService.getCourseGradeDistribution(courseId);
    }

    @GetMapping("/courses/{courseId}/completion-rate")
    public ResponseEntity<?> getCourseCompletionRate(@PathVariable String courseId) {
        return reportService.getCourseCompletionRate(courseId);
    }

    // DOCENTE
    @GetMapping("/teachers/{teacherId}/ratings")
    public ResponseEntity<?> getTeacherRatings(@PathVariable String teacherId) {
        return reportService.getTeacherRatings(teacherId);
    }

    @GetMapping("/teachers/{teacherId}/average")
    public ResponseEntity<?> getTeacherAverage(@PathVariable String teacherId) {
        return reportService.getTeacherAverage(teacherId);
    }

    @GetMapping("/teachers/{teacherId}/feedback")
    public ResponseEntity<?> getTeacherFeedback(@PathVariable String teacherId) {
        return reportService.getTeacherFeedback(teacherId);
    }

    // AVANZATI
    @GetMapping("/students/{studentId}/performance-over-time")
    public ResponseEntity<?> getStudentPerformanceOverTime(@PathVariable String studentId,
                                                           @RequestParam(required = false) String startDate,
                                                           @RequestParam(required = false) String endDate,
                                                           @RequestParam(defaultValue = "json") String format) {
        return reportService.getStudentPerformanceOverTime(studentId, startDate, endDate, format);
    }

    @GetMapping("/courses/{courseId}/performance-over-time")
    public ResponseEntity<?> getCoursePerformanceOverTime(@PathVariable String courseId,
                                                          @RequestParam(required = false) String startDate,
                                                          @RequestParam(required = false) String endDate,
                                                          @RequestParam(defaultValue = "json") String format) {
        return reportService.getCoursePerformanceOverTime(courseId, startDate, endDate, format);
    }

    @GetMapping("/teachers/{teacherId}/performance-over-time")
    public ResponseEntity<?> getTeacherPerformanceOverTime(@PathVariable String teacherId,
                                                           @RequestParam(required = false) String startDate,
                                                           @RequestParam(required = false) String endDate,
                                                           @RequestParam(defaultValue = "json") String format) {
        return reportService.getTeacherPerformanceOverTime(teacherId, startDate, endDate, format);
    }

    // GLOBALE
    @GetMapping("/summary")
    public ResponseEntity<?> getGlobalSummary() {
        return reportService.getGlobalSummary();
    }
}