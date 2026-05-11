package com.ecommerce.product.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        servers = {
                @Server(url = "http://localhost:8080", description = "API Gateway")
        },
        info = @Info(title = "Product Service API", version = "v1.0", description = "Documentation of the Product Microservice"),
        // Ceci dit à Swagger d'ajouter un petit cadenas sur chaque route
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description = "Entrez le JWT généré par le User-Service. (Ne mettez pas 'Bearer ' devant, Swagger le fait tout seul)"
)
public class OpenApiConfig {
}