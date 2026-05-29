package com.ecommerce.notification.infrastructure.adapter.out.persistence.mapper;

import com.ecommerce.notification.domain.model.Notification;
import com.ecommerce.notification.domain.model.NotificationStatus;
import com.ecommerce.notification.domain.model.NotificationType; // 👈 Nouvel import
import com.ecommerce.notification.infrastructure.adapter.out.persistence.entity.NotificationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationPersistenceMapper {

    // Domain -> JPA
    public NotificationJpaEntity toEntity(Notification domain) {
        return NotificationJpaEntity.builder()
                .id(domain.getId())
                .referenceId(domain.getReferenceId()) // 👈 Remplacé
                .type(domain.getType().name())        // 👈 Nouveau champ converti en String
                .recipient(domain.getRecipient())
                .status(domain.getStatus().name())
                .errorMessage(domain.getErrorMessage())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    // JPA -> Domain
    public Notification toDomain(NotificationJpaEntity entity) {
        return new Notification(
                entity.getId(),
                entity.getReferenceId(),              // 👈 Remplacé
                NotificationType.valueOf(entity.getType()), // 👈 Nouveau champ converti en Enum
                entity.getRecipient(),
                NotificationStatus.valueOf(entity.getStatus()),
                entity.getErrorMessage(),
                entity.getCreatedAt()
        );
    }
}