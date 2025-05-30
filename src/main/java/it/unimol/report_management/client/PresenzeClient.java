package it.unimol.report_management.client;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
public class PresenzeClient {

    public List<Map<String, Object>> getPresenzeStudente(String studentId) {
        return List.of(
                Map.of("corso", "Matematica", "percentuale", 95.0),
                Map.of("corso", "Sistemi", "percentuale", 88.5)
        );
    }

    public List<Map<String, Object>> getPresenzeCorso(String corsoId) {
        return List.of(
                Map.of("studenteId", "stud123", "percentuale", 90.0),
                Map.of("studenteId", "stud456", "percentuale", 85.0)
        );
    }
}
