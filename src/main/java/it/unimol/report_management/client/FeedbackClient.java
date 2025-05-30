package it.unimol.report_management.client;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.List;

@Component
public class FeedbackClient {

    public Map<String, Object> getFeedbackDocente(String docenteId) {
        return Map.of(
                "media", 4.6,
                "commenti", List.of("Molto preparato", "Spiegazioni chiare", "Gentile")
        );
    }

    public Map<String, Object> getFeedbackCorso(String corsoId) {
        return Map.of(
                "media", 4.2,
                "commenti", List.of("Corso interessante", "Materiale utile", "Tante esercitazioni")
        );
    }
}
