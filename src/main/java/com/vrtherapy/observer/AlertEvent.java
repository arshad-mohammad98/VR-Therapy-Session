package com.vrtherapy.observer;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Event object representing system or clinical notifications broadcast to observers.
 */
public class AlertEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String alertType;
    private final String message;
    private final Long patientId;
    private final LocalDateTime timestamp;

    public AlertEvent(String alertType, String message, Long patientId) {
        this.alertType = alertType;
        this.message = message;
        this.patientId = patientId;
        this.timestamp = LocalDateTime.now();
    }

    public String getAlertType() {
        return alertType;
    }

    public String getMessage() {
        return message;
    }

    public Long getPatientId() {
        return patientId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "AlertEvent{" +
                "alertType='" + alertType + '\'' +
                ", message='" + message + '\'' +
                ", patientId=" + patientId +
                ", timestamp=" + timestamp +
                '}';
    }
}
