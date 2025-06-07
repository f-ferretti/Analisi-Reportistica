package it.unimol.report_management.repository;

import it.unimol.report_management.model.ReportCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportCacheRepository extends JpaRepository<ReportCache, Long> {
    Optional<ReportCache> findByReportTypeAndTargetIdAndParametersAndFormat(
            String reportType, String targetId, String parameters, String format);
}