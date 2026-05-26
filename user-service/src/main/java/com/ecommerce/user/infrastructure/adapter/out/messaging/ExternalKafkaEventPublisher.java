package com.ecommerce.user.infrastructure.adapter.out.messaging;

import com.ecommerce.user.domain.port.out.ExternalEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(name = "external.kafka.enabled", havingValue = "true")
@RequiredArgsConstructor
public class ExternalKafkaEventPublisher implements ExternalEventPublisherPort {

    private final KafkaTemplate<String, Object> externalKafkaTemplate;

    @Value("${external.kafka.topic}")
    private String topic;

    @Override
    public void publish(String eventType, Object data) {
        Map<String, Object> message = new LinkedHashMap<>();
        message.put("event_type", eventType);
        message.put("event_timestamp", Instant.now().toString());
        message.put("data", data);
        externalKafkaTemplate.send(topic, message);
        log.info("External event sent: type={} to topic={}", eventType, topic);
    }
}