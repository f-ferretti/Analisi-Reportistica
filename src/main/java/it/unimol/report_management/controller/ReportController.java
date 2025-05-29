package it.unimol.report_management.controller;

import it.unimol.report_management.dto.StudentReportDTO;
import it.unimol.report_management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports/student")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/{studentId}/summary")
    public StudentReportDTO getStudentSummary(@PathVariable String studentId) {
        return reportService.generateStudentReport(studentId);
    }
}
