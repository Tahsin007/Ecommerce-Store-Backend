package com.example.ecommerce.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Component
public class TokenUtil {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    /**
     * Generate a random UUID token
     */
    public String generateUUIDToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generate a secure random token
     * @param byteLength Length of random bytes (default: 32)
     */
    public String generateSecureToken(int byteLength) {
        byte[] randomBytes = new byte[byteLength];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    /**
     * Generate a secure random token with default length (32 bytes)
     */
    public String generateSecureToken() {
        return generateSecureToken(32);
    }

    /**
     * Generate a numeric OTP
     * @param length Length of OTP (e.g., 6 for 6-digit OTP)
     */
    public String generateOTP(int length) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(secureRandom.nextInt(10));
        }
        return otp.toString();
    }

    /**
     * Generate alphanumeric code
     */
    public String generateAlphanumericCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(characters.charAt(secureRandom.nextInt(characters.length())));
        }
        return code.toString();
    }
}
