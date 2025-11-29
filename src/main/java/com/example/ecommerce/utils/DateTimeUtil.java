package com.example.ecommerce.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class DateTimeUtil {

    private static final DateTimeFormatter DEFAULT_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter TIME_FORMATTER =
        DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Get current date and time
     */
    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Format LocalDateTime to string
     */
    public String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DEFAULT_FORMATTER) : null;
    }

    /**
     * Format LocalDateTime with custom pattern
     */
    public String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    /**
     * Parse string to LocalDateTime
     */
    public LocalDateTime parse(String dateTimeString) {
        return dateTimeString != null ? LocalDateTime.parse(dateTimeString, DEFAULT_FORMATTER) : null;
    }

    /**
     * Check if date is expired
     */
    public boolean isExpired(LocalDateTime expiryDate) {
        return expiryDate != null && expiryDate.isBefore(LocalDateTime.now());
    }

    /**
     * Add days to current date
     */
    public LocalDateTime addDays(int days) {
        return LocalDateTime.now().plusDays(days);
    }

    /**
     * Add hours to current date
     */
    public LocalDateTime addHours(int hours) {
        return LocalDateTime.now().plusHours(hours);
    }

    /**
     * Add minutes to current date
     */
    public LocalDateTime addMinutes(int minutes) {
        return LocalDateTime.now().plusMinutes(minutes);
    }

    /**
     * Calculate difference in days between two dates
     */
    public long daysBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Calculate difference in hours between two dates
     */
    public long hoursBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * Convert LocalDateTime to epoch milliseconds
     */
    public long toEpochMilli(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * Get start of day
     */
    public LocalDateTime startOfDay(LocalDateTime dateTime) {
        return dateTime.toLocalDate().atStartOfDay();
    }

    /**
     * Get end of day
     */
    public LocalDateTime endOfDay(LocalDateTime dateTime) {
        return dateTime.toLocalDate().atTime(23, 59, 59);
    }
}
