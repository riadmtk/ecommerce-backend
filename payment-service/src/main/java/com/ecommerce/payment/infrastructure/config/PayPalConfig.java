package com.ecommerce.payment.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PayPalConfig {

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.mode:sandbox}")
    private String mode;

    @Bean
    public RestTemplate paypalRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public String paypalBaseUrl() {
        return mode.equalsIgnoreCase("live")
                ? "https://api-m.paypal.com"
                : "https://api-m.sandbox.paypal.com";
    }

    @Bean
    public String paypalClientId() {
        return clientId;
    }

    @Bean
    public String paypalClientSecret() {
        return clientSecret;
    }
}