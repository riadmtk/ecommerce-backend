package com.ecommerce.wishlist.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Wishlist Service API",
                version = "1.0",
                description = "Documentation de l'API du service de liste de souhaits de l'E-commerce"
        ),
        // Ceci dit "Applique cette sécurité par défaut à tous les endpoints"
        security = @SecurityRequirement(name = "Bearer Authentication")
)
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Entrez votre token JWT ici"
)
public class OpenApiConfig {
    // Cette classe est vide, les annotations font tout le travail !
}