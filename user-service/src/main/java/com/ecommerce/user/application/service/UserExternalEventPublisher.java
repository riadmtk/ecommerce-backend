package com.ecommerce.user.application.service;

import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.port.out.ExternalEventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
@ConditionalOnProperty(name = "external.kafka.enabled", havingValue = "true")
@RequiredArgsConstructor
public class UserExternalEventPublisher {

    private final ExternalEventPublisherPort externalPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserRegistered(User user) {
        externalPublisher.publish("user_created", Map.of(
                "user_id", user.getId().toString(),
                "email", user.getEmail(),
                "first_name", user.getFirstName(),
                "last_name", user.getLastName()
        ));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserLoggedIn(User user) {
        externalPublisher.publish("user_login", Map.of(
                "user_id", user.getId().toString(),
                "email", user.getEmail()
        ));
    }
}