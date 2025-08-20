package it.unimol.report_management.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI(@Value("${application.version}") String appVersion) {
        return new OpenAPI()
                .info(new Info()
                        .title("Piattaforma di Analisi e Reportistica")
                        .version(appVersion)
                        .description(
                                "Questa API consente di generare report e statistiche in formato JSON e PDF.\n" +
                                        "Gli errori sono gestiti con codici HTTP: 400 per parametri non validi, 404 per risorse non trovate, 500 per errori interni."
                        )
                        .license(new License().name("Università degli Studi del Molise").url("https://www3.unimol.it"))
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                        )
                );
    }
}