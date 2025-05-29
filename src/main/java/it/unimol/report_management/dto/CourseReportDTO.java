package it.unimol.report_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseReportDTO {
    private String courseId;
    private String courseName;
    private String courseCode;
    private Integer enrolledStudents;
    private Double averageGrade;
    private Double passRate;
    private Double attendanceRate;
    private Double assignmentCompletionRate;
    private LocalDateTime reportGeneratedAt;
}
