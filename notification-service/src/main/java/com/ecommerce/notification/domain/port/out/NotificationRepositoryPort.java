package com.ecommerce.notification.domain.port.out;

import com.ecommerce.notification.domain.model.Notification;

public interface NotificationRepositoryPort {
    Notification save(Notification notification);
}