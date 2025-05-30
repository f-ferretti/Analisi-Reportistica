package it.unimol.report_management.client;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class UtentiClient {

    public Map<String, Object> getUtenteById(String id) {
        return Map.of(
                "id", id,
                "nome", "Mario",
                "cognome", "Rossi",
                "email", "mario.rossi@studenti.unimol.it",
                "ruolo", "studente" // oppure "docente"
        );
    }
}