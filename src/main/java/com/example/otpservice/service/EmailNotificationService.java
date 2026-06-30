package com.example.otpservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@Slf4j
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendCode(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Ваш OTP код подтверждения");
            helper.setText(String.format(
                    "Здравствуйте!\n\nВаш код подтверждения: %s\n\n" +
                            "Код действителен в течение 5 минут.\n\n" +
                            "Если вы не запрашивали этот код, проигнорируйте это письмо.\n\n" +
                            "С уважением,\nСервис OTP",
                    code
            ));

            mailSender.send(message);
            log.info("OTP отправлен на email: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Не удалось отправить email на {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}