package com.vrtherapy.model;

import java.time.LocalDateTime;

/**
 * Domain entity representing real-time biometric and physiological readings
 * (heart-rate, distress score, HRV, respiration cadence).
 */
public class TelemetrySample extends BaseEntity {

    private Long liveSessionId;
    private Long patientId;
    private int heartRate;
    private int stressScore;
    private int hrv = 65;
    private int breathingRate = 16;
    private LocalDateTime recordedAt;

    public TelemetrySample() {
        super();
        this.recordedAt = LocalDateTime.now();
    }

    public TelemetrySample(Long liveSessionId, Long patientId, int heartRate, int stressScore, int hrv, int breathingRate) {
        super();
        this.liveSessionId = liveSessionId;
        this.patientId = patientId;
        this.heartRate = heartRate;
        this.stressScore = stressScore;
        this.hrv = hrv;
        this.breathingRate = breathingRate;
        this.recordedAt = LocalDateTime.now();
    }

    public Long getLiveSessionId() {
        return liveSessionId;
    }

    public void setLiveSessionId(Long liveSessionId) {
        this.liveSessionId = liveSessionId;
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

    public int getStressScore() {
        return stressScore;
    }

    public void setStressScore(int stressScore) {
        this.stressScore = stressScore;
    }

    public int getHrv() {
        return hrv;
    }

    public void setHrv(int hrv) {
        this.hrv = hrv;
    }

    public int getBreathingRate() {
        return breathingRate;
    }

    public void setBreathingRate(int breathingRate) {
        this.breathingRate = breathingRate;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}
