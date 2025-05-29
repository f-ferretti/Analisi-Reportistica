package it.unimol.report_management.service.impl;

import it.unimol.report_management.dto.*;
import it.unimol.report_management.service.ReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Override
    public StudentReportDTO generateStudentReport(String studentId) {
        return new StudentReportDTO(studentId, "Mario Rossi", "mario.rossi@studenti.unimol.it",
                6, 12, 27.3, 89.5, 10, 12, LocalDateTime.now());
    }

    @Override
    public List<Map<String, String>> getCoursesByStudent(String studentId) {
        return List.of(Map.of("courseId", "INF001", "name", "Basi di Dati"));
    }

    @Override
    public List<Map<String, Object>> getAssignmentsByStudent(String studentId) {
        return List.of(Map.of("title", "Compito 1", "status", "consegnato", "grade", 28));
    }

    @Override
    public List<Map<String, Object>> getExamsByStudent(String studentId) {
        return List.of(Map.of("examId", "EX123", "course", "Basi di Dati", "grade", 30));
    }

    @Override
    public CourseReportDTO generateCourseReport(String courseId) {
        return new CourseReportDTO(courseId, "Programmazione I", "INF001",
                45, 24.8, 78.5, 82.3, 88.9, LocalDateTime.now());
    }

    @Override
    public List<Map<String, Object>> getGradesByCourse(String courseId) {
        return List.of(Map.of("studentId", "123", "grade", 27));
    }

    @Override
    public List<Map<String, Object>> getAttendanceByCourse(String courseId) {
        return List.of(Map.of("studentId", "123", "attendanceRate", 92.5));
    }

    @Override
    public List<Map<String, Object>> getAssignmentsByCourse(String courseId) {
        return List.of(Map.of("assignment", "Progetto finale", "completionRate", 87.0));
    }

    @Override
    public TeacherReportDTO generateTeacherReport(String teacherId) {
        return new TeacherReportDTO(teacherId, "Prof. Giovanni Bianchi", "g.bianchi@unimol.it",
                3, 120, 4.2, 85.0, LocalDateTime.now());
    }

    @Override
    public List<Map<String, Object>> getGradesGivenByTeacher(String teacherId) {
        return List.of(Map.of("course", "Basi di Dati", "avgGrade", 28.3));
    }

    @Override
    public List<Map<String, Object>> getFeedbacksForTeacher(String teacherId) {
        return List.of(Map.of("avgRating", 4.5, "comments", List.of("Molto chiaro", "Disponibile")));
    }

    @Override
    public StudentReportDTO generateStudentReportFromRequest(ReportRequestDTO requestDTO) {
        return generateStudentReport(requestDTO.getTargetId());
    }

    @Override
    public CourseReportDTO generateCourseReportFromRequest(ReportRequestDTO requestDTO) {
        return generateCourseReport(requestDTO.getTargetId());
    }

    @Override
    public TeacherReportDTO generateTeacherReportFromRequest(ReportRequestDTO requestDTO) {
        return generateTeacherReport(requestDTO.getTargetId());
    }

    @Override
    public String exportReport(ReportRequestDTO requestDTO) {
        return "Exported report of type '" + requestDTO.getReportType() + "' for " + requestDTO.getTargetId();
    }
}
