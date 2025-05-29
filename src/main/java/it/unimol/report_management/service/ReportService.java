package it.unimol.report_management.service;

import it.unimol.report_management.dto.*;

import java.util.List;
import java.util.Map;

public interface ReportService {
    // Studente
    StudentReportDTO generateStudentReport(String studentId);
    List<Map<String, String>> getCoursesByStudent(String studentId);
    List<Map<String, Object>> getAssignmentsByStudent(String studentId);
    List<Map<String, Object>> getExamsByStudent(String studentId);

    // Corso
    CourseReportDTO generateCourseReport(String courseId);
    List<Map<String, Object>> getGradesByCourse(String courseId);
    List<Map<String, Object>> getAttendanceByCourse(String courseId);
    List<Map<String, Object>> getAssignmentsByCourse(String courseId);

    // Docente
    TeacherReportDTO generateTeacherReport(String teacherId);
    List<Map<String, Object>> getGradesGivenByTeacher(String teacherId);
    List<Map<String, Object>> getFeedbacksForTeacher(String teacherId);

    // POST / export
    StudentReportDTO generateStudentReportFromRequest(ReportRequestDTO requestDTO);
    CourseReportDTO generateCourseReportFromRequest(ReportRequestDTO requestDTO);
    TeacherReportDTO generateTeacherReportFromRequest(ReportRequestDTO requestDTO);
    String exportReport(ReportRequestDTO requestDTO);
}
