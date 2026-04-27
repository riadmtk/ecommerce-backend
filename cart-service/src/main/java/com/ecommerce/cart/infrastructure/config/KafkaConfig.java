package com.ecommerce.cart.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    // 1. Inject the value from your application.yml
    @Value("${kafka.topics.cart-checked-out}")
    private String cartCheckedOutTopic;

    @Bean
    public NewTopic cartCheckoutTopic() {
        return TopicBuilder.name(cartCheckedOutTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}