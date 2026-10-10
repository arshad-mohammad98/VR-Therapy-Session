package com.vrtherapy.strategy;


/**
 * Triggers a crisis alert if elevated biometrics (HR > 115 and stress > 7) persist over 3 consecutive telemetry pulses.
 */
public class ProlongedElevationCrisisStrategy implements CrisisDetectionStrategy {

    @Override
    public String getRuleName() {
        return "PROLONGED_ELEVATED_AROUSAL";
    }

    @Override
    public boolean evaluate(CrisisContext context) {
        return context.getConsecutiveSpikeCount() >= 3
                && context.getCurrentHeartRate() > 115
                && context.getCurrentStressLevel() >= 7;
    }

    @Override
    public String getAlertMessage(CrisisContext context) {
        return String.format("WARNING: Prolonged sustained arousal detected over %d cycles (HR %d bpm, Stress %d/10). Calming breath-sphere triggered.",
                context.getConsecutiveSpikeCount(), context.getCurrentHeartRate(), context.getCurrentStressLevel());
    }
}
