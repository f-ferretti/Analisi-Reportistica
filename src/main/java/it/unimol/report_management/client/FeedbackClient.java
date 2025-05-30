package it.unimol.report_management.client;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
public class FeedbackClient {

    public List<Map<String, Object>> getValutazioniByStudent(String studentId) {
        return List.of(
                Map.of("corso", "Basi di Dati", "voto", 30),
                Map.of("corso", "Programmazione I", "voto", 27)
        );
    }

    public List<Map<String, Object>> getValutazioniByDocente(String teacherId) {
        return List.of(
                Map.of("corso", "Sistemi Operativi", "mediaVoti", 28.5)
        );
    }
}