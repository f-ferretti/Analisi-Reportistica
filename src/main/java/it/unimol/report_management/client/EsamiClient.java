package it.unimol.report_management.client;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
public class EsamiClient {

    public List<Map<String, Object>> getEsamiSostenuti(String studentId) {
        return List.of(
                Map.of("esameId", "EX001", "corso", "Basi di Dati", "voto", 30),
                Map.of("esameId", "EX002", "corso", "Reti", "voto", 28)
        );
    }

    public List<Map<String, Object>> getEsitiEsameCorso(String corsoId) {
        return List.of(
                Map.of("studenteId", "stud123", "voto", 27),
                Map.of("studenteId", "stud456", "voto", 29)
        );
    }
}