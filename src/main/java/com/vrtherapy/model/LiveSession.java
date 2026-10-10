package com.vrtherapy.model;

import java.time.LocalDateTime;

/**
 * Domain entity representing an active/live therapy session room.
 * Manages runtime parameters and bidirectional telemetry and commands.
 */
public class LiveSession extends BaseEntity {

    private Long sessionId;
    private Long patientId;
    private Long therapistId;
    private LiveSessionState state = LiveSessionState.SCHEDULED;
    private EnvironmentType currentEnvironment;
    private int currentIntensity = 1;
    private boolean breathingGuideActive = true;
    private int elapsedSeconds = 0;
    private int currentHeartRate = 75;
    private int currentStressScore = 4;
    private String inVrMessage;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    public LiveSession() {
        super();
    }

    public LiveSession(Long sessionId, Long patientId, Long therapistId, EnvironmentType currentEnvironment, int currentIntensity) {
        super();
        this.sessionId = sessionId;
        this.patientId = patientId;
        this.therapistId = therapistId;
        this.currentEnvironment = currentEnvironment;
        this.currentIntensity = currentIntensity;
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

    public Long getTherapistId() {
        return therapistId;
    }

    public void setTherapistId(Long therapistId) {
        this.therapistId = therapistId;
    }

    public LiveSessionState getState() {
        return state;
    }

    public void setState(LiveSessionState state) {
        this.state = state;
    }

    public EnvironmentType getCurrentEnvironment() {
        return currentEnvironment;
    }

    public void setCurrentEnvironment(EnvironmentType currentEnvironment) {
        this.currentEnvironment = currentEnvironment;
    }

    public int getCurrentIntensity() {
        return currentIntensity;
    }

    public void setCurrentIntensity(int currentIntensity) {
        this.currentIntensity = currentIntensity;
    }

    public boolean isBreathingGuideActive() {
        return breathingGuideActive;
    }

    public void setBreathingGuideActive(boolean breathingGuideActive) {
        this.breathingGuideActive = breathingGuideActive;
    }

    public int getElapsedSeconds() {
        return elapsedSeconds;
    }

    public void setElapsedSeconds(int elapsedSeconds) {
        this.elapsedSeconds = elapsedSeconds;
    }

    public int getCurrentHeartRate() {
        return currentHeartRate;
    }

    public void setCurrentHeartRate(int currentHeartRate) {
        this.currentHeartRate = currentHeartRate;
    }

    public int getCurrentStressScore() {
        return currentStressScore;
    }

    public void setCurrentStressScore(int currentStressScore) {
        this.currentStressScore = currentStressScore;
    }

    public String getInVrMessage() {
        return inVrMessage;
    }

    public void setInVrMessage(String inVrMessage) {
        this.inVrMessage = inVrMessage;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }
}
