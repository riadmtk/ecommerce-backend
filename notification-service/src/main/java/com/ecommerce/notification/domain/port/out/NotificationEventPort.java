package com.ecommerce.notification.domain.port.out;

import com.ecommerce.notification.domain.model.Notification;

public interface NotificationEventPort {
    void publishNotificationSentEvent(Notification notification);
    void publishNotificationFailedEvent(Notification notification);
}