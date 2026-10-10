package com.vrtherapy.util;

import com.vrtherapy.exception.ValidationException;
import java.time.LocalDateTime;

/**
 * Domain-specific programmatic validation helpers.
 */
public final class ValidationUtil {

    private ValidationUtil() {
    }

    /**
     * Validates therapy session parameters.
     */
    public static void validateSessionParameters(String title, Integer durationMinutes, Integer intensityLevel) {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Session title must not be blank.");
        }
        if (durationMinutes == null || durationMinutes < 5 || durationMinutes > 90) {
            throw new ValidationException("Session duration must be between 5 and 90 minutes.");
        }
        if (intensityLevel == null || intensityLevel < 1 || intensityLevel > 5) {
            throw new ValidationException("Intensity level must be an integer between 1 and 5.");
        }
    }

    /**
     * Validates scheduled appointment time.
     */
    public static void validateAppointmentSchedule(LocalDateTime scheduledTime) {
        if (scheduledTime == null) {
            throw new ValidationException("Appointment time is required.");
        }
        if (scheduledTime.isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new ValidationException("Cannot schedule an appointment in the past.");
        }
    }

    /**
     * Validates subjective ratings.
     */
    public static void validateStressScore(Integer score, String context) {
        if (score == null || score < 1 || score > 10) {
            throw new ValidationException(context + " stress score must be between 1 and 10.");
        }
    }
}
