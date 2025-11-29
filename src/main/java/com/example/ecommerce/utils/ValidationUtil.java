package com.example.ecommerce.utils;
import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_-]{3,50}$"
    );

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9]{10,15}$"
    );

    /**
     * Validates email format
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates username format (alphanumeric, underscore, hyphen, 3-50 chars)
     */
    public boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    /**
     * Validates strong password
     * - At least 8 characters
     * - Contains at least one digit
     * - Contains at least one lowercase letter
     * - Contains at least one uppercase letter
     * - Contains at least one special character
     * - No whitespace
     */
    public boolean isStrongPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Validates phone number format
     */
    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phoneNumber.trim()).matches();
    }

    /**
     * Sanitizes string input to prevent XSS attacks
     */
    public String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.trim()
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll("\"", "&quot;")
            .replaceAll("'", "&#x27;")
            .replaceAll("/", "&#x2F;");
    }

    /**
     * Validates if string is not null or empty
     */
    public boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validates string length is within range
     */
    public boolean isLengthValid(String value, int minLength, int maxLength) {
        if (value == null) {
            return false;
        }
        int length = value.trim().length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Password strength checker
     * Returns: 0 (weak), 1 (medium), 2 (strong)
     */
    public int getPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return 0;
        }

        int strength = 0;

        // Check length
        if (password.length() >= 8) strength++;
        if (password.length() >= 12) strength++;

        // Check for numbers
        if (password.matches(".*\\d.*")) strength++;

        // Check for lowercase
        if (password.matches(".*[a-z].*")) strength++;

        // Check for uppercase
        if (password.matches(".*[A-Z].*")) strength++;

        // Check for special characters
        if (password.matches(".*[@#$%^&+=!*()_\\-{}\\[\\]:;\"'<>,.?/|\\\\].*")) strength++;

        if (strength <= 2) return 0; // Weak
        if (strength <= 4) return 1; // Medium
        return 2; // Strong
    }

    /**
     * Validates URL format
     */
    public boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        try {
            new java.net.URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if value is numeric
     */
    public boolean isNumeric(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
