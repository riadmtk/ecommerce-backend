package com.ecommerce.notification.domain.port.out;

public interface SmsPort {
    void sendSms(String to, String message);
}