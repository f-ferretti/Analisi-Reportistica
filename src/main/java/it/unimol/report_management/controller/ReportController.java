package it.unimol.report_management.controller;

import it.unimol.report_management.dto.*;
import it.unimol.report_management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // GET - STUDENT
    @GetMapping("/student/{studentId}/summary")
    public StudentReportDTO getStudentSummary(@PathVariable String studentId) {
        return reportService.generateStudentReport(studentId);
    }

    // GET - COURSE
    @GetMapping("/course/{courseId}/summary")
    public CourseReportDTO getCourseSummary(@PathVariable String courseId) {
        return reportService.generateCourseReport(courseId);
    }

    // GET - TEACHER
    @GetMapping("/teacher/{teacherId}/summary")
    public TeacherReportDTO getTeacherSummary(@PathVariable String teacherId) {
        return reportService.generateTeacherReport(teacherId);
    }

    // POST - STUDENT REPORT PERSONALIZZATO
    @PostMapping("/student")
    public StudentReportDTO generateStudentReport(@RequestBody ReportRequestDTO request) {
        return reportService.generateStudentReportFromRequest(request);
    }

    // POST - COURSE REPORT PERSONALIZZATO
    @PostMapping("/course")
    public CourseReportDTO generateCourseReport(@RequestBody ReportRequestDTO request) {
        return reportService.generateCourseReportFromRequest(request);
    }

    // POST - EXPORT REPORT
    @PostMapping("/export")
    public String exportReport(@RequestBody ReportRequestDTO request) {
        return reportService.exportReport(request);
    }
}
