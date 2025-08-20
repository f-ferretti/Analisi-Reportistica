package it.unimol.report_management.dto.teacher;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "Indicatori di consistenza anno-su-anno per docente/corso")
public class TeacherConsistencyDTO {

    private String teacherId;
    private String courseCode;
    private Integer from;
    private Integer to;
    private Integer yearsCount;
    private Map<Integer, Double> avgByYear;
    private Map<Integer, Double> passRateByYear;
    private Double stddev;
    private Double trendSlope;

    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public Integer getFrom() { return from; }
    public void setFrom(Integer from) { this.from = from; }

    public Integer getTo() { return to; }
    public void setTo(Integer to) { this.to = to; }

    public Integer getYearsCount() { return yearsCount; }
    public void setYearsCount(Integer yearsCount) { this.yearsCount = yearsCount; }

    public Map<Integer, Double> getAvgByYear() { return avgByYear; }
    public void setAvgByYear(Map<Integer, Double> avgByYear) { this.avgByYear = avgByYear; }

    public Map<Integer, Double> getPassRateByYear() { return passRateByYear; }
    public void setPassRateByYear(Map<Integer, Double> passRateByYear) { this.passRateByYear = passRateByYear; }

    public Double getStddev() { return stddev; }
    public void setStddev(Double stddev) { this.stddev = stddev; }

    public Double getTrendSlope() { return trendSlope; }
    public void setTrendSlope(Double trendSlope) { this.trendSlope = trendSlope; }
}
