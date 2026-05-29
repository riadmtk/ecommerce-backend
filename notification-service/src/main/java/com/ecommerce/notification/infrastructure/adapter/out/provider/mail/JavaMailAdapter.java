package com.ecommerce.notification.infrastructure.adapter.out.provider.mail;

import com.ecommerce.notification.domain.port.out.EmailPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JavaMailAdapter implements EmailPort {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {
        log.info("⏳ Attempting to send email to {}", to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@comcom.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        // This will throw an exception if the SMTP server is down,
        // which will be caught by your NotificationService!
        mailSender.send(message);
        log.info("✅ Email successfully sent to: {}", to);
    }
}