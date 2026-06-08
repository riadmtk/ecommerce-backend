package com.ecommerce.user.infrastructure.adapter.out.feign;

import com.ecommerce.user.infrastructure.adapter.out.feign.dto.VerificationCodeRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "${notification.service.url:http://localhost:8087}")
public interface NotificationServiceClient {
    @PostMapping("/api/v1/notifications/send-verification")
    void sendVerificationCode(@RequestBody VerificationCodeRequest request);
}