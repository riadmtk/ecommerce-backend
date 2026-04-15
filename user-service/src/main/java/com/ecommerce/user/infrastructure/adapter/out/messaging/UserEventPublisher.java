package com.ecommerce.user.infrastructure.adapter.out.messaging;

import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.port.out.UserEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventPublisher implements UserEventPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.user-registered}")
    private String userRegisteredTopic;

    @Override
    public void publishUserRegistered(User user) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "UserRegistered");
        event.put("userId", user.getId().toString());
        event.put("email", user.getEmail());
        event.put("firstName", user.getFirstName());
        event.put("lastName", user.getLastName());
        event.put("occurredAt", LocalDateTime.now().toString());

        kafkaTemplate.send(userRegisteredTopic, user.getId().toString(), event);

        log.info("Event UserRegistered publié pour userId: {}", user.getId());
    }
}