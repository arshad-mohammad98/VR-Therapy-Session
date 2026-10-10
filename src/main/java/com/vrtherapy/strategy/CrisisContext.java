package com.vrtherapy.strategy;

import com.vrtherapy.model.LiveSession;
import com.vrtherapy.model.User;

/**
 * Context object encapsulating patient biometrics, current live session parameters, and physiological readings.
 */
public class CrisisContext {

    private final LiveSession liveSession;
    private final User patient;
    private final int currentHeartRate;
    private final int currentStressLevel;
    private final int consecutiveSpikeCount;

    public CrisisContext(LiveSession liveSession, User patient, int currentHeartRate, int currentStressLevel, int consecutiveSpikeCount) {
        this.liveSession = liveSession;
        this.patient = patient;
        this.currentHeartRate = currentHeartRate;
        this.currentStressLevel = currentStressLevel;
        this.consecutiveSpikeCount = consecutiveSpikeCount;
    }

    public LiveSession getLiveSession() {
        return liveSession;
    }

    public User getPatient() {
        return patient;
    }

    public int getCurrentHeartRate() {
        return currentHeartRate;
    }

    public int getCurrentStressLevel() {
        return currentStressLevel;
    }

    public int getConsecutiveSpikeCount() {
        return consecutiveSpikeCount;
    }
}
