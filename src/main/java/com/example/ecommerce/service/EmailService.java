package com.example.ecommerce.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public Mono<Void> sendVerificationEmail(String to, String token) {
        return Mono.fromRunnable(() -> {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(to);
                message.setSubject("Email Verification");
                message.setText("Please verify your email by clicking the link below:\n\n" +
                    frontendUrl + "/verify-email?token=" + token +
                    "\n\nThis link will expire in 24 hours.");

                mailSender.send(message);
                log.info("Verification email sent to: {}", to);
            } catch (Exception e) {
                log.error("Failed to send verification email", e);
                throw new RuntimeException("Failed to send verification email", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    public Mono<Void> sendPasswordResetEmail(String to, String token) {
        return Mono.fromRunnable(() -> {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(to);
                message.setSubject("Password Reset");
                message.setText("Reset your password by clicking the link below:\n\n" +
                    frontendUrl + "/reset-password?token=" + token +
                    "\n\nThis link will expire in 24 hours.\n\n" +
                    "If you didn't request this, please ignore this email.");

                mailSender.send(message);
                log.info("Password reset email sent to: {}", to);
            } catch (Exception e) {
                log.error("Failed to send password reset email", e);
                throw new RuntimeException("Failed to send password reset email", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
