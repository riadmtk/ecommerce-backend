package com.ecommerce.notification.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {

    private final UUID id; // Final because the ID should never change
    private final String referenceId;
    private final NotificationType type;
    private final String recipient;
    private NotificationStatus status;
    private String errorMessage;
    private final LocalDateTime createdAt;

    // 1. Constructor to create a NEW notification (Used by your Business Logic)
    public Notification(String referenceId, String recipient, NotificationType type) {
        this.id = UUID.randomUUID();
        this.referenceId = referenceId;
        this.type = type;
        this.recipient = recipient;
        this.status = NotificationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // 2. Constructor to RECONSTITUTE an existing notification (Used ONLY by your Persistence Mapper)
    public Notification(UUID id, String referenceId, NotificationType type, String recipient, NotificationStatus status, String errorMessage, LocalDateTime createdAt) {
        this.id = id;
        this.referenceId = referenceId;
        this.type = type;
        this.recipient = recipient;
        this.status = status;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
    }

    // --- Rich Business Behaviors (Not just dumb setters!) ---

    public void markAsSent() {
        this.status = NotificationStatus.SENT;
    }

    public void markAsFailed(String reason) {
        this.status = NotificationStatus.FAILED;
        this.errorMessage = reason;
    }

    // --- Getters ---
    public UUID getId() { return id; }
    public String getReferenceId() { return referenceId; }
    public NotificationType getType() { return type; }
    public String getRecipient() { return recipient; }
    public NotificationStatus getStatus() { return status; }
    public String getErrorMessage() { return errorMessage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}