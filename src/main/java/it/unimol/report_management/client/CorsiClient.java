package it.unimol.report_management.client;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
public class CorsiClient {

    public Map<String, Object> getCorsoById(String corsoId) {
        return Map.of(
                "id", corsoId,
                "nome", "Programmazione I",
                "anno", 1,
                "docenteId", "doc123"
        );
    }

    public List<String> getStudentiIscritti(String corsoId) {
        return List.of("stud123", "stud456", "stud789");
    }

    public List<String> getCorsiDocente(String docenteId) {
        return List.of("INF001", "INF002");
    }
}