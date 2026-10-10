package com.vrtherapy.dto;

public class TelemetryPulseDto {

    private Long sessionId;
    private Long patientId;
    private int heartRate = 72;
    private int stressLevel = 5;

    public TelemetryPulseDto() {
    }

    public TelemetryPulseDto(Long sessionId, Long patientId, int heartRate, int stressLevel) {
        this.sessionId = sessionId;
        this.patientId = patientId;
        this.heartRate = heartRate;
        this.stressLevel = stressLevel;
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

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getStressLevel() {
        return stressLevel;
    }

    public void setStressLevel(int stressLevel) {
        this.stressLevel = stressLevel;
    }
}
