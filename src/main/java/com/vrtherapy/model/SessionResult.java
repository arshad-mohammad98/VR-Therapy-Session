package com.vrtherapy.model;

import java.time.LocalDateTime;

/**
 * Domain entity representing the clinical outcome and biometrics recorded upon session conclusion.
 */
public class SessionResult extends BaseEntity {

    private Long sessionId;
    private Long patientId;
    private int preStressScore;
    private int postStressScore;
    private int durationSeconds;
    private int averageHeartRate = 75;
    private int peakHeartRate = 85;
    private boolean emergencyStopped = false;
    private String stopReason;
    private String completionNotes;
    private LocalDateTime completedAt;

    public SessionResult() {
        super();
        this.completedAt = LocalDateTime.now();
    }

    public SessionResult(Long sessionId, Long patientId, int preStressScore, int postStressScore,
                         int durationSeconds, int averageHeartRate, int peakHeartRate) {
        super();
        this.sessionId = sessionId;
        this.patientId = patientId;
        this.preStressScore = preStressScore;
        this.postStressScore = postStressScore;
        this.durationSeconds = durationSeconds;
        this.averageHeartRate = averageHeartRate;
        this.peakHeartRate = peakHeartRate;
        this.completedAt = LocalDateTime.now();
    }

    public int getStressDelta() {
        return postStressScore - preStressScore;
    }

    public int getPreStressRating() {
        return preStressScore;
    }

    public int getPostStressRating() {
        return postStressScore;
    }

    private Double improvementPercentageOverride = null;

    public double getImprovementPercentage() {
        if (improvementPercentageOverride != null) {
            return improvementPercentageOverride;
        }
        if (preStressScore == 0) return 0.0;
        return ((double) (preStressScore - postStressScore) / preStressScore) * 100.0;
    }

    public void setImprovementPercentage(double improvementPercentage) {
        this.improvementPercentageOverride = improvementPercentage;
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

    public int getPreStressScore() {
        return preStressScore;
    }

    public void setPreStressScore(int preStressScore) {
        this.preStressScore = preStressScore;
    }

    public int getPostStressScore() {
        return postStressScore;
    }

    public void setPostStressScore(int postStressScore) {
        this.postStressScore = postStressScore;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public int getAverageHeartRate() {
        return averageHeartRate;
    }

    public void setAverageHeartRate(int averageHeartRate) {
        this.averageHeartRate = averageHeartRate;
    }

    public int getPeakHeartRate() {
        return peakHeartRate;
    }

    public void setPeakHeartRate(int peakHeartRate) {
        this.peakHeartRate = peakHeartRate;
    }

    public boolean isEmergencyStopped() {
        return emergencyStopped;
    }

    public void setEmergencyStopped(boolean emergencyStopped) {
        this.emergencyStopped = emergencyStopped;
    }

    public String getStopReason() {
        return stopReason;
    }

    public void setStopReason(String stopReason) {
        this.stopReason = stopReason;
    }

    public String getCompletionNotes() {
        return completionNotes;
    }

    public void setCompletionNotes(String completionNotes) {
        this.completionNotes = completionNotes;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
