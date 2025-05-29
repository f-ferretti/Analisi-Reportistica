package it.unimol.report_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.unimol.report_management.dto.*;
import it.unimol.report_management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // STUDENTE

    @Operation(summary = "Genera il report riassuntivo di uno studente")
    @ApiResponse(responseCode = "200", description = "Report generato correttamente")
    @GetMapping("/student/{studentId}/summary")
    public StudentReportDTO getStudentSummary(@PathVariable String studentId) {
        return reportService.generateStudentReport(studentId);
    }

    @Operation(summary = "Restituisce i corsi frequentati da uno studente")
    @GetMapping("/student/{studentId}/courses")
    public List<Map<String, String>> getStudentCourses(@PathVariable String studentId) {
        return reportService.getCoursesByStudent(studentId);
    }

    @Operation(summary = "Restituisce i compiti consegnati da uno studente")
    @GetMapping("/student/{studentId}/assignments")
    public List<Map<String, Object>> getStudentAssignments(@PathVariable String studentId) {
        return reportService.getAssignmentsByStudent(studentId);
    }

    @Operation(summary = "Restituisce gli esami sostenuti da uno studente")
    @GetMapping("/student/{studentId}/exams")
    public List<Map<String, Object>> getStudentExams(@PathVariable String studentId) {
        return reportService.getExamsByStudent(studentId);
    }

    @Operation(summary = "Genera un report avanzato per uno studente (POST)")
    @PostMapping("/student")
    public StudentReportDTO generateStudentReport(@RequestBody ReportRequestDTO request) {
        return reportService.generateStudentReportFromRequest(request);
    }

    // CORSO

    @Operation(summary = "Genera il report riassuntivo di un corso")
    @GetMapping("/course/{courseId}/summary")
    public CourseReportDTO getCourseSummary(@PathVariable String courseId) {
        return reportService.generateCourseReport(courseId);
    }

    @Operation(summary = "Restituisce i voti degli studenti in un corso")
    @GetMapping("/course/{courseId}/grades")
    public List<Map<String, Object>> getCourseGrades(@PathVariable String courseId) {
        return reportService.getGradesByCourse(courseId);
    }

    @Operation(summary = "Restituisce le presenze degli studenti in un corso")
    @GetMapping("/course/{courseId}/attendance")
    public List<Map<String, Object>> getCourseAttendance(@PathVariable String courseId) {
        return reportService.getAttendanceByCourse(courseId);
    }

    @Operation(summary = "Restituisce i dati sui compiti di un corso")
    @GetMapping("/course/{courseId}/assignments")
    public List<Map<String, Object>> getCourseAssignments(@PathVariable String courseId) {
        return reportService.getAssignmentsByCourse(courseId);
    }

    @Operation(summary = "Genera un report avanzato per un corso (POST)")
    @PostMapping("/course")
    public CourseReportDTO generateCourseReport(@RequestBody ReportRequestDTO request) {
        return reportService.generateCourseReportFromRequest(request);
    }

    // DOCENTE

    @Operation(summary = "Genera il report riassuntivo di un docente")
    @GetMapping("/teacher/{teacherId}/summary")
    public TeacherReportDTO getTeacherSummary(@PathVariable String teacherId) {
        return reportService.generateTeacherReport(teacherId);
    }

    @Operation(summary = "Restituisce i voti assegnati da un docente")
    @GetMapping("/teacher/{teacherId}/given-grades")
    public List<Map<String, Object>> getGradesGivenByTeacher(@PathVariable String teacherId) {
        return reportService.getGradesGivenByTeacher(teacherId);
    }

    @Operation(summary = "Restituisce i feedback ricevuti da un docente")
    @GetMapping("/teacher/{teacherId}/feedbacks")
    public List<Map<String, Object>> getTeacherFeedbacks(@PathVariable String teacherId) {
        return reportService.getFeedbacksForTeacher(teacherId);
    }

    // EXPORT

    @Operation(summary = "Esporta un report in formato specifico (PDF, JSON, ecc.)")
    @PostMapping("/export")
    public String exportReport(@RequestBody ReportRequestDTO request) {
        return reportService.exportReport(request);
    }
}