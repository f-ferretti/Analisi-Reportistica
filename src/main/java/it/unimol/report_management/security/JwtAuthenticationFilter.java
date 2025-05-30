package it.unimol.report_management.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import io.jsonwebtoken.*;

@Component
public class JwtAuthenticationFilter implements Filter {

    private static final String PUBLIC_KEY_PATH = "src/main/resources/public_key.pem";
    private PublicKey publicKey;

    public JwtAuthenticationFilter() {
        try {
            this.publicKey = loadPublicKey();
        } catch (Exception e) {
            throw new RuntimeException("Errore nel caricamento della chiave pubblica", e);
        }
    }

    private PublicKey loadPublicKey() throws Exception {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream("public_key.pem")) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Chiave pubblica non trovata nel classpath");
            }

            String key = new String(inputStream.readAllBytes())
                    .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePublic(spec);
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ((HttpServletResponse) res).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token mancante o malformato");
            return;
        }

        String token = authHeader.substring(7);
        try {
            Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);
            chain.doFilter(req, res);
        } catch (JwtException e) {
            ((HttpServletResponse) res).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token non valido");
        }
    }
}