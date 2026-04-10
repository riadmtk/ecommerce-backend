package com.ecommerce.user.application.service;

import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.port.out.UserEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventService {

    private final UserEventPublisherPort eventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegistered(User user) {
        eventPublisher.publishUserRegistered(user);
        log.debug("Événement UserRegistered publié pour userId: {}", user.getId());
    }
}