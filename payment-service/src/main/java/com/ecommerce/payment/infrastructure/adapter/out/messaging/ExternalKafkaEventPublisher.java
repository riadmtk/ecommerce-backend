package com.ecommerce.payment.infrastructure.adapter.out.messaging;

import com.ecommerce.payment.domain.port.out.ExternalEventPublisherPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(name = "external.kafka.enabled", havingValue = "true")
public class ExternalKafkaEventPublisher implements ExternalEventPublisherPort {

    private final KafkaTemplate<String, Object> externalKafkaTemplate;

    @Value("${external.kafka.topic}")
    private String topic;

    public ExternalKafkaEventPublisher(
            @Qualifier("externalKafkaTemplate") KafkaTemplate<String, Object> externalKafkaTemplate) {
        this.externalKafkaTemplate = externalKafkaTemplate;
    }

    @Override
    public void publishEvent(String eventType, Object data) {
        Map<String, Object> message = new HashMap<>();
        message.put("event_type", eventType);
        message.put("event_timestamp", Instant.now().toString());
        message.put("data", data);

        externalKafkaTemplate.send(topic, eventType, message);
        log.info("[ExternalKafka] Événement publié : type={} topic={}", eventType, topic);
    }
}