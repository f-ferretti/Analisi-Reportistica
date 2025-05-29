package it.unimol.report_management.controller;

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
    @GetMapping("/student/{studentId}/summary")
    public StudentReportDTO getStudentSummary(@PathVariable String studentId) {
        return reportService.generateStudentReport(studentId);
    }

    @GetMapping("/student/{studentId}/courses")
    public List<Map<String, String>> getStudentCourses(@PathVariable String studentId) {
        return reportService.getCoursesByStudent(studentId);
    }

    @GetMapping("/student/{studentId}/assignments")
    public List<Map<String, Object>> getStudentAssignments(@PathVariable String studentId) {
        return reportService.getAssignmentsByStudent(studentId);
    }

    @GetMapping("/student/{studentId}/exams")
    public List<Map<String, Object>> getStudentExams(@PathVariable String studentId) {
        return reportService.getExamsByStudent(studentId);
    }

    @PostMapping("/student")
    public StudentReportDTO generateStudentReport(@RequestBody ReportRequestDTO request) {
        return reportService.generateStudentReportFromRequest(request);
    }

    // CORSO
    @GetMapping("/course/{courseId}/summary")
    public CourseReportDTO getCourseSummary(@PathVariable String courseId) {
        return reportService.generateCourseReport(courseId);
    }

    @GetMapping("/course/{courseId}/grades")
    public List<Map<String, Object>> getCourseGrades(@PathVariable String courseId) {
        return reportService.getGradesByCourse(courseId);
    }

    @GetMapping("/course/{courseId}/attendance")
    public List<Map<String, Object>> getCourseAttendance(@PathVariable String courseId) {
        return reportService.getAttendanceByCourse(courseId);
    }

    @GetMapping("/course/{courseId}/assignments")
    public List<Map<String, Object>> getCourseAssignments(@PathVariable String courseId) {
        return reportService.getAssignmentsByCourse(courseId);
    }

    @PostMapping("/course")
    public CourseReportDTO generateCourseReport(@RequestBody ReportRequestDTO request) {
        return reportService.generateCourseReportFromRequest(request);
    }

    // DOCENTE
    @GetMapping("/teacher/{teacherId}/summary")
    public TeacherReportDTO getTeacherSummary(@PathVariable String teacherId) {
        return reportService.generateTeacherReport(teacherId);
    }

    @GetMapping("/teacher/{teacherId}/given-grades")
    public List<Map<String, Object>> getGradesGivenByTeacher(@PathVariable String teacherId) {
        return reportService.getGradesGivenByTeacher(teacherId);
    }

    @GetMapping("/teacher/{teacherId}/feedbacks")
    public List<Map<String, Object>> getTeacherFeedbacks(@PathVariable String teacherId) {
        return reportService.getFeedbacksForTeacher(teacherId);
    }

    @PostMapping("/export")
    public String exportReport(@RequestBody ReportRequestDTO request) {
        return reportService.exportReport(request);
    }
}
