package it.unimol.report_management.security;

import it.unimol.report_management.service.jwt.TokenJWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final TokenJWTService tokenJWTService;

    public JwtInterceptor(TokenJWTService tokenJWTService) {
        this.tokenJWTService = tokenJWTService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token mancante o malformato");
            return false;
        }

        String token = authHeader.substring(7); // rimuove "Bearer "

        try {
            tokenJWTService.validateToken(token); // verifica con chiave pubblica
            return true;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token non valido: " + e.getMessage());
            return false;
        }
    }
}