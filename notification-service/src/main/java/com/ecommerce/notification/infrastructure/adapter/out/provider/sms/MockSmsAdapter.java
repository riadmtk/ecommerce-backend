package com.ecommerce.notification.infrastructure.adapter.out.provider.sms;

import com.ecommerce.notification.domain.port.out.SmsPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockSmsAdapter implements SmsPort {

    @Override
    public void sendSms(String to, String message) {
        log.info("=================================================");
        log.info("📱 [MOCK SMS ADAPTER] Sending SMS...");
        log.info("To: {}", to);
        log.info("Message: {}", message);
        log.info("Status: DELIVERED");
        log.info("=================================================");
    }
}