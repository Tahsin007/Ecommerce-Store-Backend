package com.example.ecommerce.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    public Mono<Void> sendVerificationEmail(String email, String token) {
        final String link = frontendUrl + "/verify-email?token=" + token;
        return Mono.fromRunnable(() -> {
                try {
                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

                    String htmlContent = buildVerificationEmail(link);

                    helper.setFrom(fromEmail);
                    helper.setTo(email);
                    helper.setSubject("Verify Your Account");
                    helper.setText(htmlContent, true); // true = HTML

                    mailSender.send(message);
                    log.info("Verification email sent to {}", email);
                } catch (MessagingException e) {
                    log.error("Failed to send verification email to {}: {}", email, e.getMessage());
                    // fallback for development
                    log.info("DEV MODE: verification link {}", link);
                    throw new RuntimeException(e);
                }
            }).subscribeOn(Schedulers.boundedElastic())
            .doOnError(e -> log.error("Error sending verification email", e))
            .then();
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

    private String buildVerificationEmail(String link) {
        return """
                <html>
                  <body style="font-family: Arial, sans-serif; background-color: #f4f4f7; padding: 30px; color: #333;">
                    <table style="max-width: 600px; margin: auto; background-color: #fff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05);">
                      <tr>
                        <td style="padding: 30px; text-align: center;">
                          <h2 style="color: #2d2d2d;">Welcome to <span style="color: #007BFF;">Our Ecommerce Store</span>!</h2>
                          <p style="font-size: 16px; line-height: 1.5; margin: 20px 0;">
                            Thank you for signing up! Please confirm your email address to activate your account.
                          </p>
                          <a href="%s"
                             style="display: inline-block; background-color: #007BFF; color: #fff; text-decoration: none;
                                    padding: 12px 25px; border-radius: 6px; font-weight: bold;">
                            Verify Account
                          </a>
                          <p style="font-size: 14px; color: #888; margin-top: 25px;">
                            Or copy and paste this link in your browser:<br/>
                            <a href="%s" style="color: #007BFF;">%s</a>
                          </p>
                        </td>
                      </tr>
                    </table>
                    <p style="text-align:center; font-size:12px; color:#aaa; margin-top:20px;">
                      © 2025 Ecommerce Store. All rights reserved.
                    </p>
                  </body>
                </html>
                """.formatted(link, link, link);
    }
}
