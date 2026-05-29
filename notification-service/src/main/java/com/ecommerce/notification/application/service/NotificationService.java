package com.ecommerce.notification.application.service;

import com.ecommerce.notification.domain.model.Notification;
import com.ecommerce.notification.domain.model.NotificationType;
import com.ecommerce.notification.domain.port.in.SendAccountWelcomeUseCase;
import com.ecommerce.notification.domain.port.in.SendOrderConfirmationUseCase;
import com.ecommerce.notification.domain.port.in.SendPasswordResetUseCase;
import com.ecommerce.notification.domain.port.in.SendRestockAlertUseCase;
import com.ecommerce.notification.domain.port.out.EmailPort;
import com.ecommerce.notification.domain.port.out.NotificationEventPort;
import com.ecommerce.notification.domain.port.out.NotificationRepositoryPort;
import com.ecommerce.notification.domain.port.out.SmsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService implements
        SendOrderConfirmationUseCase,
        SendAccountWelcomeUseCase,
        SendPasswordResetUseCase,
        SendRestockAlertUseCase {

    private final EmailPort emailPort;
    private final SmsPort smsPort;
    private final NotificationRepositoryPort repositoryPort;
    private final NotificationEventPort eventPort;

    // --- USE CASE 1 : ORDER CONFIRMATION ---
    @Override
    public void sendConfirmation(String orderId, String email, String phoneNumber) {
        Notification notification = new Notification(orderId, email, NotificationType.ORDER_CONFIRMATION);
        repositoryPort.save(notification);

        try {
            String body = "Hello!\n\nYour order " + orderId + " has been confirmed.";
            emailPort.sendEmail(email, "Order Confirmation #" + orderId, body);

            String smsBody = "Your order " + orderId + " is confirmed!";
            // smsPort.sendSms(phoneNumber, smsBody);

            notification.markAsSent();
            repositoryPort.save(notification);
            eventPort.publishNotificationSentEvent(notification);

        } catch (Exception e) {
            notification.markAsFailed(e.getMessage());
            repositoryPort.save(notification);
            eventPort.publishNotificationFailedEvent(notification);
        }
    }

    // --- USE CASE 2 : ACCOUNT WELCOME ---
    @Override
    public void sendWelcome(String userId, String email, String fullName) {
        Notification notification = new Notification(userId, email, NotificationType.ACCOUNT_WELCOME);
        repositoryPort.save(notification);

        try {
            String subject = "Welcome " + fullName + "!";
            String body = "Hello " + fullName + ",\n\nYour account has been successfully created. Thank you for joining us!";

            emailPort.sendEmail(email, subject, body);

            notification.markAsSent();
            repositoryPort.save(notification);
            eventPort.publishNotificationSentEvent(notification);

        } catch (Exception e) {
            notification.markAsFailed(e.getMessage());
            repositoryPort.save(notification);
            eventPort.publishNotificationFailedEvent(notification);
        }
    }

    // --- USE CASE 3 : PASSWORD RESET ---
    @Override
    public void sendPasswordReset(String userId, String email, String resetLink) {
        // 🎯 On utilise le NotificationType approprié
        Notification notification = new Notification(userId, email, NotificationType.PASSWORD_RESET);
        repositoryPort.save(notification);

        try {
            String subject = "Password Reset Request";
            String body = "Hello,\n\nYou requested to reset your password. " +
                    "Please click on the secure link below:\n\n" + resetLink +
                    "\n\nIf you did not make this request, please ignore this email.";

            emailPort.sendEmail(email, subject, body);

            notification.markAsSent();
            repositoryPort.save(notification);
            eventPort.publishNotificationSentEvent(notification);

        } catch (Exception e) {
            notification.markAsFailed(e.getMessage());
            repositoryPort.save(notification);
            eventPort.publishNotificationFailedEvent(notification);
        }
    }

    @Override
    public void sendRestockAlert(String userId, String email, String productId, String productName) {
        Notification notification = new Notification(userId, email, NotificationType.RESTOCK_ALERT);
        repositoryPort.save(notification);

        try {
            String subject = "Great news! " + productName + " is back in stock!";
            String body = "Hello,\n\nThe item you've been waiting for is finally back!\n" +
                    "Product: " + productName + "\n\n" +
                    "Hurry up and grab it before it sells out again!\n\n" +
                    "Best,\nYour E-commerce Team";

            emailPort.sendEmail(email, subject, body);

            notification.markAsSent();
            repositoryPort.save(notification);
            eventPort.publishNotificationSentEvent(notification);

        } catch (Exception e) {
            notification.markAsFailed(e.getMessage());
            repositoryPort.save(notification);
            eventPort.publishNotificationFailedEvent(notification);
        }
    }
}