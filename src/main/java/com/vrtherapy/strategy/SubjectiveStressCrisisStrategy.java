package com.vrtherapy.strategy;


/**
 * Triggers a crisis alert if subjective stress score reaches 9 or 10 on the 10-point scale.
 */
public class SubjectiveStressCrisisStrategy implements CrisisDetectionStrategy {

    private static final int STRESS_CRISIS_THRESHOLD = 9;

    @Override
    public String getRuleName() {
        return "SUBJECTIVE_STRESS_ACUTE_SPIKE";
    }

    @Override
    public boolean evaluate(CrisisContext context) {
        return context.getCurrentStressLevel() >= STRESS_CRISIS_THRESHOLD;
    }

    @Override
    public String getAlertMessage(CrisisContext context) {
        return String.format("ALERT: Patient self-reported acute panic level (%d/10). Grounding protocol initiated.",
                context.getCurrentStressLevel());
    }
}
