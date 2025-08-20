package it.unimol.report_management.service;

import it.unimol.report_management.dto.student.ExamResultDTO;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public interface CourseReportService {

    // Aggregato per anno (dallo stub). Se annoAccademico != null → filtrato.
    List<Map<String, Integer>> enrollmentsByYear(String courseCode);

    // Voti del corso (usa lo stub che già hai)
    List<ExamResultDTO> courseGrades(String courseCode, Integer annoAccademico);

    // Grafico per PDF
    BufferedImage courseGradesDistributionChart(String courseCode, Integer annoAccademico);
}