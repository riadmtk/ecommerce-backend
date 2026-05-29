package com.ecommerce.notification.infrastructure.adapter.out.persistence;

import com.ecommerce.notification.domain.model.Notification;
import com.ecommerce.notification.domain.port.out.NotificationRepositoryPort;
import com.ecommerce.notification.infrastructure.adapter.out.persistence.entity.NotificationJpaEntity;
import com.ecommerce.notification.infrastructure.adapter.out.persistence.mapper.NotificationPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationPersistenceAdapter implements NotificationRepositoryPort {

    private final NotificationJpaRepository repository;
    private final NotificationPersistenceMapper mapper;

    @Override
    public Notification save(Notification notification) {
        // 1. Convertir le modèle du domaine en entité DB
        NotificationJpaEntity entity = mapper.toEntity(notification);

        // 2. Sauvegarder via Spring Data
        NotificationJpaEntity savedEntity = repository.save(entity);

        // 3. Reconvertir en modèle de domaine pur et le renvoyer
        return mapper.toDomain(savedEntity);
    }
}