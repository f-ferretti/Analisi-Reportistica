package it.unimol.report_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentReportDTO {
    private String studentId;
    private String studentName;
    private String studentEmail;
    private Integer enrolledCourses;
    private Integer completedExams;
    private Double averageGrade;
    private Double attendanceRate;
    private Integer assignmentsSubmitted;
    private Integer assignmentsTotal;
    private LocalDateTime reportGeneratedAt;
}
