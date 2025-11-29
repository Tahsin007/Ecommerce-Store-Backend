package com.example.ecommerce.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplateUtil {

    @Value("${app.name}")
    private String appName;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.support-email}")
    private String supportEmail;

    public String getVerificationEmailTemplate(String recipientName, String token) {
        String verificationLink = frontendUrl + "/verify-email?token=" + token;

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .button { display: inline-block; padding: 12px 24px; background-color: #4CAF50; 
                             color: white; text-decoration: none; border-radius: 4px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>%s</h1>
                    </div>
                    <div class="content">
                        <h2>Welcome %s!</h2>
                        <p>Thank you for registering with %s. Please verify your email address by clicking the button below:</p>
                        <p style="text-align: center;">
                            <a href="%s" class="button">Verify Email Address</a>
                        </p>
                        <p>Or copy and paste this link into your browser:</p>
                        <p style="word-break: break-all;">%s</p>
                        <p><strong>This link will expire in 24 hours.</strong></p>
                        <p>If you didn't create an account with us, please ignore this email.</p>
                    </div>
                    <div class="footer">
                        <p>© 2024 %s. All rights reserved.</p>
                        <p>Need help? Contact us at <a href="mailto:%s">%s</a></p>
                    </div>
                </div>
            </body>
            </html>
            """, appName, recipientName != null ? recipientName : "User", appName,
            verificationLink, verificationLink, appName, supportEmail, supportEmail);
    }

    public String getPasswordResetEmailTemplate(String recipientName, String token) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #FF5722; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .button { display: inline-block; padding: 12px 24px; background-color: #FF5722; 
                             color: white; text-decoration: none; border-radius: 4px; margin: 20px 0; }
                    .warning { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 12px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Password Reset Request</h1>
                    </div>
                    <div class="content">
                        <h2>Hi %s,</h2>
                        <p>We received a request to reset your password for your %s account.</p>
                        <p>Click the button below to reset your password:</p>
                        <p style="text-align: center;">
                            <a href="%s" class="button">Reset Password</a>
                        </p>
                        <p>Or copy and paste this link into your browser:</p>
                        <p style="word-break: break-all;">%s</p>
                        <div class="warning">
                            <strong>⚠️ Important:</strong>
                            <ul>
                                <li>This link will expire in 24 hours</li>
                                <li>If you didn't request this, please ignore this email</li>
                                <li>Your password will remain unchanged until you create a new one</li>
                            </ul>
                        </div>
                    </div>
                    <div class="footer">
                        <p>© 2024 %s. All rights reserved.</p>
                        <p>Need help? Contact us at <a href="mailto:%s">%s</a></p>
                    </div>
                </div>
            </body>
            </html>
            """, recipientName != null ? recipientName : "User", appName,
            resetLink, resetLink, appName, supportEmail, supportEmail);
    }

    public String getWelcomeEmailTemplate(String recipientName) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #2196F3; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome to %s!</h1>
                    </div>
                    <div class="content">
                        <h2>Hi %s,</h2>
                        <p>Your email has been successfully verified! Welcome to %s.</p>
                        <p>You can now access all features of our platform.</p>
                        <h3>What's Next?</h3>
                        <ul>
                            <li>Browse our product catalog</li>
                            <li>Update your profile information</li>
                            <li>Start shopping!</li>
                        </ul>
                        <p>If you have any questions, feel free to reach out to our support team.</p>
                    </div>
                    <div class="footer">
                        <p>© 2024 %s. All rights reserved.</p>
                        <p>Contact us at <a href="mailto:%s">%s</a></p>
                    </div>
                </div>
            </body>
            </html>
            """, appName, recipientName != null ? recipientName : "User",
            appName, appName, supportEmail, supportEmail);
    }
}
