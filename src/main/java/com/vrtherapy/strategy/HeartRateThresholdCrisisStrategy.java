package com.vrtherapy.strategy;


/**
 * Triggers a crisis alert if patient heart rate spikes above 135 bpm.
 */
public class HeartRateThresholdCrisisStrategy implements CrisisDetectionStrategy {

    private static final int HR_CRISIS_CEILING = 135;

    @Override
    public String getRuleName() {
        return "HEART_RATE_CRITICAL_CEILING";
    }

    @Override
    public boolean evaluate(CrisisContext context) {
        return context.getCurrentHeartRate() >= HR_CRISIS_CEILING;
    }

    @Override
    public String getAlertMessage(CrisisContext context) {
        return String.format("CRITICAL: Heart rate surged to %d bpm (safety ceiling %d bpm). Auto-pause recommended.",
                context.getCurrentHeartRate(), HR_CRISIS_CEILING);
    }
}
