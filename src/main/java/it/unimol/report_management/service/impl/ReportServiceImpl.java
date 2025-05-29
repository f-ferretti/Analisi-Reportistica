package it.unimol.report_management.service.impl;

import it.unimol.report_management.dto.StudentReportDTO;
import it.unimol.report_management.service.ReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReportServiceImpl implements ReportService {

    @Override
    public StudentReportDTO generateStudentReport(String studentId) {
        // Mock temporaneo in attesa dei client ai microservizi esterni
        return new StudentReportDTO(
                studentId,
                "Mario Rossi",
                "mario.rossi@studenti.unimol.it",
                6,
                12,
                27.3,
                89.5,
                10,
                12,
                LocalDateTime.now()
        );
    }
}
