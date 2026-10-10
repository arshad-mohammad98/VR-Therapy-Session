package com.vrtherapy.model;

import java.time.LocalDateTime;

/**
 * Domain entity representing patient mood journal entries.
 */
public class MoodEntry extends BaseEntity {

    private Long patientId;
    private int score;
    private String primaryEmotion;
    private String notes;
    private LocalDateTime recordedAt;

    public MoodEntry() {
        super();
        this.recordedAt = LocalDateTime.now();
    }

    public MoodEntry(Long patientId, int score, String primaryEmotion, String notes) {
        super();
        this.patientId = patientId;
        this.score = score;
        this.primaryEmotion = primaryEmotion;
        this.notes = notes;
        this.recordedAt = LocalDateTime.now();
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getPrimaryEmotion() {
        return primaryEmotion;
    }

    public void setPrimaryEmotion(String primaryEmotion) {
        this.primaryEmotion = primaryEmotion;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}
