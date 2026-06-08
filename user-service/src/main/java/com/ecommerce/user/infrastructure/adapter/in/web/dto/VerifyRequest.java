package com.ecommerce.user.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyRequest(
        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Format email invalide")
        String email,

        @NotBlank(message = "Le code de vérification est obligatoire")
        @Pattern(regexp = "^\\d{6}$", message = "Le code doit contenir exactement 6 chiffres")
        String code
) {}