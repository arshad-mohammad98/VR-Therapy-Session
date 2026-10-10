package com.vrtherapy.model;

import java.time.LocalDate;

/**
 * Domain entity representing inter-session therapeutic tasks and self-practice assignments.
 */
public class Homework extends BaseEntity {

    private Long patientId;
    private Long therapistId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private boolean completed = false;
    private boolean reminderSent = false;

    public Homework() {
        super();
    }

    public Homework(Long patientId, Long therapistId, String title, String description, LocalDate dueDate) {
        super();
        this.patientId = patientId;
        this.therapistId = therapistId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getTherapistId() {
        return therapistId;
    }

    public void setTherapistId(Long therapistId) {
        this.therapistId = therapistId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isReminderSent() {
        return reminderSent;
    }

    public void setReminderSent(boolean reminderSent) {
        this.reminderSent = reminderSent;
    }
}
