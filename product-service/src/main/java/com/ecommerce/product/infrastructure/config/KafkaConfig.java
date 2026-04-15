package com.ecommerce.product.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaConfig {

    // 1. Définition du Topic que ce service va utiliser/écouter
    public static final String TOPIC_ORDER_CREATED = "order-created-topic";
    public static final String TOPIC_PRODUCT_EVENTS = "product-events-topic";

    // 2. Spring va automatiquement créer ces topics dans Kafka au démarrage s'ils n'existent pas
    @Bean
    public NewTopic productEventsTopic() {
        return TopicBuilder.name(TOPIC_PRODUCT_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(TOPIC_ORDER_CREATED)
                .partitions(3)
                .replicas(1)
                .build();
    }
}