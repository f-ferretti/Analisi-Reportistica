package it.unimol.report_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherReportDTO {
    private String teacherId;
    private String teacherName;
    private String teacherEmail;
    private Integer coursesTeaching;
    private Integer totalStudents;
    private Double averageFeedback;
    private Double responseRate;
    private LocalDateTime reportGeneratedAt;
}
