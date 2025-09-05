package it.unimol.report_management.service;

import java.util.Map;
import java.util.Optional;

/**
 * Servizio minimale di caching su database (tabella REPORT_CACHE).
 * Non usa alcun provider esterno: solo Spring Data JPA.
 */
public interface CacheService {

    Optional<byte[]> get(String reportType, String targetId, Map<String, ?> params, String format);

    void put(String reportType, String targetId, Map<String, ?> params, String format, byte[] data);

    /** Rimuove entry più vecchie di TTL configurata (utile per cron/manuale). Restituisce quante. */
    long evictExpired();

    /** Rimuove tutte le entry per un certo tipo (manutenzione). */
    long evictByType(String reportType);

    /** Svuota completamente la cache. */
    void clear();
}