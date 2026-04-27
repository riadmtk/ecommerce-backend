package com.ecommerce.product.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaConfig {

    // 1. Inject the values directly from your application.yml
    @Value("${kafka.topics.product-events}")
    private String productEventsTopic;

    // Assuming you also have this defined in your YAML!
    // If your YAML key is different, update the string inside the @Value annotation.
    @Value("${kafka.topics.order-events}")
    private String orderCreatedTopic;

    // 2. Use the injected variables to build the topics
    @Bean
    public NewTopic productEventsTopicBean() {
        return TopicBuilder.name(productEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderCreatedTopicBean() {
        return TopicBuilder.name(orderCreatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}