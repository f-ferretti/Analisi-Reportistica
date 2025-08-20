package it.unimol.report_management.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Abilita le annotazioni di sicurezza a livello metodo (@PreAuthorize, @PostAuthorize, ecc.).
 * Non modifica la SecurityFilterChain esistente.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig {
}
