package it.unimol.report_management.service;

import it.unimol.report_management.dto.StudentReportDTO;

public interface ReportService {
    StudentReportDTO generateStudentReport(String studentId);
}
