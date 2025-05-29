package it.unimol.report_management.service.impl;

import it.unimol.report_management.dto.*;
import it.unimol.report_management.service.ReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReportServiceImpl implements ReportService {

    @Override
    public StudentReportDTO generateStudentReport(String studentId) {
        return new StudentReportDTO(
                studentId,
                "Mario Rossi",
                "mario.rossi@studenti.unimol.it",
                6, 12,
                27.3, 89.5,
                10, 12,
                LocalDateTime.now()
        );
    }

    @Override
    public CourseReportDTO generateCourseReport(String courseId) {
        return new CourseReportDTO(
                courseId,
                "Programmazione I",
                "INF001",
                45, 24.8,
                78.5, 82.3, 88.9,
                LocalDateTime.now()
        );
    }

    @Override
    public TeacherReportDTO generateTeacherReport(String teacherId) {
        return new TeacherReportDTO(
                teacherId,
                "Prof. Giovanni Bianchi",
                "g.bianchi@unimol.it",
                3, 120,
                4.2, 85.0,
                LocalDateTime.now()
        );
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
        // In un'app reale genererebbe un file PDF o invierebbe messaggi
        return "Exported report of type '" + requestDTO.getReportType() + "' for " + requestDTO.getTargetId();
    }
}
