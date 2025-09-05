package it.unimol.report_management.service;

import it.unimol.report_management.model.ReportCache;
import it.unimol.report_management.repository.ReportCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final ReportCacheRepository repo;

    /** TTL in minuti, configurabile con 'cache.ttl-minutes' (default 60). */
    @Value("${cache.ttl-minutes:60}")
    private long ttlMinutes;

    private String canonicalParams(Map<String, ?> params) {
        if (params == null || params.isEmpty()) return "";
        return params.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + String.valueOf(e.getValue()))
                .collect(Collectors.joining("&"));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<byte[]> get(String reportType, String targetId, Map<String, ?> params, String format) {
        String p = canonicalParams(params);
        return repo.findByReportTypeAndTargetIdAndParametersAndFormat(reportType, targetId, p, format)
                .filter(rc -> rc.getGeneratedAt() != null &&
                        rc.getGeneratedAt().isAfter(LocalDateTime.now().minusMinutes(ttlMinutes)))
                .map(ReportCache::getReportData);
    }

    @Override
    @Transactional
    public void put(String reportType, String targetId, Map<String, ?> params, String format, byte[] data) {
        String p = canonicalParams(params);
        // upsert semplice: se esiste lo sovrascrivo
        ReportCache rc = repo.findByReportTypeAndTargetIdAndParametersAndFormat(reportType, targetId, p, format)
                .orElseGet(ReportCache::new);
        rc.setReportType(reportType);
        rc.setTargetId(targetId);
        rc.setParameters(p);
        rc.setFormat(format);
        rc.setReportData(data);
        rc.setGeneratedAt(LocalDateTime.now());
        repo.save(rc);
    }

    @Override
    @Transactional
    public long evictExpired() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(ttlMinutes);
        return repo.deleteByGeneratedAtBefore(threshold);
    }

    @Override
    @Transactional
    public long evictByType(String reportType) {
        return repo.deleteByReportType(reportType);
    }

    @Override
    @Transactional
    public void clear() {
        repo.deleteAllInBatch();
    }
}