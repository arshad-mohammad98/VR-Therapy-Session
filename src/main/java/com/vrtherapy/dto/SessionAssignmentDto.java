package com.vrtherapy.dto;

import java.time.LocalDateTime;

/**
 * Data transfer object for assigning a session to a patient and scheduling the session.
 */
public class SessionAssignmentDto {

    private Long id;
    private Long sessionId;
    private Long patientId;
    private LocalDateTime scheduledTime;

    private String therapistInstructions;

    public SessionAssignmentDto() {
        this.scheduledTime = LocalDateTime.now().plusHours(2);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getTherapistInstructions() {
        return therapistInstructions;
    }

    public void setTherapistInstructions(String therapistInstructions) {
        this.therapistInstructions = therapistInstructions;
    }
}
