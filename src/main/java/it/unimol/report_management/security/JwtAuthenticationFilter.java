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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Filtro JWT con:
 * - mapping coerente: roles -> ROLE_*, authorities così come sono
 * - dedup delle authorities
 * - claims e raw token esposti per /debug/auth (request attributes + authentication details)
 * - log dettagliati per capire i 403
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final TokenJWTService tokenService;

    public JwtAuthenticationFilter(TokenJWTService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader)) {
            if (log.isDebugEnabled()) {
                log.debug("JWT: header Authorization assente (path={})", request.getRequestURI());
            }
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(authHeader);
        if (!StringUtils.hasText(token)) {
            if (log.isDebugEnabled()) {
                log.debug("JWT: header Authorization presente ma formato non valido: {}", previewHeader(authHeader));
            }
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Validazione token + estrazione claims
            Claims claims = tokenService.validateToken(token);
            String subject = claims.getSubject() != null ? claims.getSubject()
                    : Objects.toString(claims.get("sub"), "anonymous");

            // Estrai authorities dai 2 claim supportati
            List<GrantedAuthority> authorities = new ArrayList<>();

            // 1) Claim "authorities": es. ["ROLE_ADMIN", "PERM_X"]
            Object authoritiesClaim = claims.get("authorities");
            for (String a : extractStringList(authoritiesClaim)) {
                if (StringUtils.hasText(a)) {
                    authorities.add(new SimpleGrantedAuthority(a.trim()));
                }
            }

            // 2) Claim "roles": es. ["ADMIN"] -> mappa a "ROLE_ADMIN"
            Object rolesClaim = claims.get("roles");
            for (String r : extractStringList(rolesClaim)) {
                if (StringUtils.hasText(r)) {
                    String role = r.trim();
                    String withPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                    authorities.add(new SimpleGrantedAuthority(withPrefix));
                }
            }

            // Dedup
            authorities = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(StringUtils::hasText)
                    .distinct()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // Costruisci Authentication
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(subject, null, authorities);

            // Attacca i claims ai details per debug
            authentication.setDetails(Collections.unmodifiableMap(new LinkedHashMap<>(claims)));

            // Metti in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Espone a /debug/auth
            request.setAttribute("jwt.token", token);
            request.setAttribute("jwt.claims", claims);

            // Log utile
            if (log.isInfoEnabled()) {
                log.info("JWT OK - sub='{}' | authorities={}", subject,
                        authorities.stream().map(GrantedAuthority::getAuthority).toList());
            }

        } catch (JwtException e) {
            log.warn("JWT NON VALIDO ({}) path={} | header={}",
                    e.getMessage(), request.getRequestURI(), previewHeader(authHeader));
        } catch (Exception e) {
            log.error("Errore durante la validazione JWT (path={}): {}", request.getRequestURI(), e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    // --- Helpers ---

    private String resolveToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) return null;
        if (authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7).trim();
        }
        // Tollerante: se Swagger/cliente invia solo il token senza "Bearer "
        if (authorizationHeader.split("\\.").length == 3) {
            return authorizationHeader.trim();
        }
        return null;
    }

    private String previewHeader(String header) {
        if (header == null) return "null";
        if (header.length() <= 24) return header;
        return header.substring(0, 24) + "…";
    }

    /**
     * Converte un claim in lista di stringhe.
     * Supporta: List, array, stringa CSV. Null -> lista vuota.
     */
    @SuppressWarnings("unchecked")
    private List<String> extractStringList(Object claimValue) {
        if (claimValue == null) return Collections.emptyList();

        if (claimValue instanceof List<?> list) {
            List<String> out = new ArrayList<>(list.size());
            for (Object o : list) {
                if (o != null) out.add(o.toString());
            }
            return out;
        }
        if (claimValue.getClass().isArray()) {
            Object[] arr = (Object[]) claimValue;
            List<String> out = new ArrayList<>(arr.length);
            for (Object o : arr) out.add(Objects.toString(o, ""));
            return out;
        }
        if (claimValue instanceof String s) {
            if (!StringUtils.hasText(s)) return Collections.emptyList();
            String[] parts = s.split(",");
            List<String> out = new ArrayList<>(parts.length);
            for (String p : parts) {
                String t = p.trim();
                if (!t.isEmpty()) out.add(t);
            }
            return out;
        }
        return List.of(claimValue.toString());
    }
}