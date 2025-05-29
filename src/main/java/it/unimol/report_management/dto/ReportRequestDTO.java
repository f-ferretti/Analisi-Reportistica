package it.unimol.report_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDTO {
    private String reportType;
    private String targetId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String format;
}
