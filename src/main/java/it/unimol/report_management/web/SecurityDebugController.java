package it.unimol.report_management.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class SecurityDebugController {

    /**
     * Endpoint di debug (permitAll nella SecurityConfig) che mostra quello che
     * Spring Security "vede" sull'utente corrente. Chiamalo da Swagger
     * incollando il tuo JWT in Authorize. Se le authorities non contengono
     * "ROLE_ADMIN", allora gli @PreAuthorize("hasRole('ADMIN')") daranno 403.
     */
    @GetMapping("/debug/auth")
    public ResponseEntity<Map<String, Object>> debugAuth(Authentication authentication,
                                                         HttpServletRequest request) {

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("path", request.getRequestURI());

        if (authentication == null) {
            out.put("authenticated", false);
            out.put("message", "Nessuna Authentication presente (token mancante o non valido)");
            return ResponseEntity.ok(out);
        }

        out.put("authenticated", authentication.isAuthenticated());
        out.put("principal", authentication.getName());

        List<String> authorities = authentication.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).toList();
        out.put("authorities", authorities);

        // claims allegati come "details" dal filtro
        Object details = authentication.getDetails();
        if (details instanceof Map<?, ?> map) {
            out.put("claims", map);
        } else {
            out.put("claims", "non disponibili");
        }

        // token originale se presente (utile per capire se Swagger l'ha inviato)
        Object rawToken = request.getAttribute("jwt.token");
        out.put("rawTokenPresent", rawToken != null);

        return ResponseEntity.ok(out);
    }
}