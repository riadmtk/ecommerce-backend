package com.ecommerce.user.infrastructure.adapter.out.feign.dto;

public record VerificationCodeRequest(String email, String code) {}