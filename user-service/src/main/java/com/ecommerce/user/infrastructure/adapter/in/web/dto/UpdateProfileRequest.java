package com.ecommerce.user.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        @Size(min = 2, max = 50)
        String firstName,

        @Size(min = 2, max = 50)
        String lastName,

        String phoneNumber
) {}