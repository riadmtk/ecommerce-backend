package com.ecommerce.user.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendCodeRequest(
        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Format email invalide")
        String email
) {}