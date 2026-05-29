package com.ecommerce.notification.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String referenceId; // 👈 Remplacé orderId

    @Column(nullable = false)
    private String type; // 👈 Nouveau champ (ORDER_CONFIRMATION, ACCOUNT_WELCOME, etc.)

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String status; // PENDING, SENT, FAILED

    @Column(length = 1000)
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}