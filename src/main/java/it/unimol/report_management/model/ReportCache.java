package it.unimol.report_management.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;

@Entity
@Table(name = "report_cache")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_type", nullable = false)
    private String reportType;

    @Column(name = "target_id", nullable = false)
    private String targetId;

    @Column(name = "parameters", nullable = false)
    private String parameters;

    @Column(name = "format", nullable = false)
    private String format;

    @Column(name = "report_data", columnDefinition = "bytea", nullable = false)
    private byte[] reportData;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    public String getReportDataAsJson() {
        return new String(this.reportData, StandardCharsets.UTF_8);
    }
}