package it.unimol.report_management.security;

import it.unimol.report_management.service.jwt.TokenJWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final TokenJWTService tokenService;

    public JwtAuthenticationFilter(TokenJWTService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // Validazione del token e estrazione delle claims
                Claims claims = tokenService.validateToken(token);
                String username = claims.getSubject();

                // Recupero eventuali ruoli dalla claim "roles"
                List<SimpleGrantedAuthority> authorities = List.of();
                Object rolesObj = claims.get("roles");
                if (rolesObj instanceof List<?>) {
                    @SuppressWarnings("unchecked")
                    List<String> roles = (List<String>) rolesObj;
                    authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                }

                // Costruisco l'Authentication e lo setto nel contesto di Spring Security
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.debug("Utente autenticato: {}", username);
            } catch (JwtException e) {
                logger.warn("Token JWT non valido: {}", e.getMessage());
                // Se vuoi bloccare subito la richiesta con 401, decommenta le righe seguenti:
                // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT non valido");
                // return;
            }
        } else {
            logger.debug("Nessun Authorization header o formato non valido");
        }

        // Proseguimento della catena di filtri
        filterChain.doFilter(request, response);
    }
}