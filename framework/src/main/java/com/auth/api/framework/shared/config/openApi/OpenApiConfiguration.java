package com.auth.api.framework.shared.config.openApi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI financialApi() {

        return new OpenAPI()
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "Bearer Authentication",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )

                .info(new Info()
                        .title("Financial API")
                        .description("""
                                Financial API built with Clean Architecture
                                and Hexagonal Architecture.
                                """)
                        .version("v1")
                        .contact(new Contact()
                                .name("Gustavo Ventieri")
                                .email("seu@email.com")
                                .url("https://github.com/seuusuario"))
                        .license(new License()
                                .name("MIT")))

                .externalDocs(
                        new ExternalDocumentation()
                                .description("Project Repository")
                                .url("https://github.com/seuusuario/financial-api")
                );
    }
}
