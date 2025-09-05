package it.unimol.report_management.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Cache persistente (PostgreSQL) per payload costosi (PDF/JSON/PNG...).
 * Chiave logica: (reportType, targetId, parameters, format).
 */
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

    @Column(name = "report_type", nullable = false, length = 64)
    private String reportType;

    @Column(name = "target_id", nullable = false, length = 64)
    private String targetId;

    /** Stringa dei parametri canonica: k1=v1&k2=v2... (chiavi ordinate). */
    @Column(name = "parameters", nullable = false, length = 512)
    private String parameters;

    @Column(name = "format", nullable = false, length = 16)
    private String format;

    /**
     * Dati del report.
     * IMPORTANTE: niente @Lob qui, forziamo il mapping a BYTEA.
     */
    @JdbcTypeCode(SqlTypes.VARBINARY) // Hibernate 6 → mappa correttamente su BYTEA in Postgres
    @Column(name = "report_data", nullable = false, columnDefinition = "bytea")
    private byte[] reportData;

    /** Quando è stato generato (per TTL/invalidazione). */
    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    /** Helper comodo quando il payload è JSON. */
    public String getReportDataAsJson() {
        return new String(this.reportData, StandardCharsets.UTF_8);
    }
}