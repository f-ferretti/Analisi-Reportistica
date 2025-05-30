package it.unimol.report_management.client;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
public class CompitiClient {

    public List<Map<String, Object>> getCompitiStudente(String studentId) {
        return List.of(
                Map.of("titolo", "Compito 1", "corso", "Algoritmi", "stato", "consegnato", "voto", 28),
                Map.of("titolo", "Compito 2", "corso", "Algoritmi", "stato", "mancante", "voto", null)
        );
    }

    public List<Map<String, Object>> getCompitiCorso(String corsoId) {
        return List.of(
                Map.of("titolo", "Compito 1", "completamento", 90.0),
                Map.of("titolo", "Progetto Finale", "completamento", 85.5)
        );
    }
}