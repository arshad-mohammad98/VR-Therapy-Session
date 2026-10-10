package com.vrtherapy.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain entity representing an assignment of a therapy session to a patient.
 */
public class SessionAssignment extends BaseEntity {

    private Long patientId;
    private Long sessionId;
    private Long planId;
    private AssignmentStatus status = AssignmentStatus.ASSIGNED;
    private LocalDate scheduledDate;
    private LocalDateTime assignedAt;

    public SessionAssignment() {
        super();
        this.assignedAt = LocalDateTime.now();
    }

    public SessionAssignment(Long patientId, Long sessionId, Long planId, AssignmentStatus status, LocalDate scheduledDate) {
        super();
        this.patientId = patientId;
        this.sessionId = sessionId;
        this.planId = planId;
        this.status = status;
        this.scheduledDate = scheduledDate;
        this.assignedAt = LocalDateTime.now();
    }

    public SessionAssignment(Long sessionId, Long patientId, LocalDateTime scheduledTime, String notes) {
        super();
        this.sessionId = sessionId;
        this.patientId = patientId;
        this.scheduledDate = scheduledTime != null ? scheduledTime.toLocalDate() : LocalDate.now();
        this.assignedAt = LocalDateTime.now();
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
}
