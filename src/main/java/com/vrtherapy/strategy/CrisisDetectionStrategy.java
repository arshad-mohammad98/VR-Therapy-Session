package com.vrtherapy.strategy;

/**
 * Strategy interface evaluating whether incoming telemetry violates clinical safety thresholds.
 */
public interface CrisisDetectionStrategy {

    /**
     * Unique identifier for the crisis detection rule.
     */
    String getRuleName();

    /**
     * Assesses whether a crisis condition is met given the current physiological context.
     *
     * @param context the real-time biometric and session state
     * @return true if safety threshold breached
     */
    boolean evaluate(CrisisContext context);

    /**
     * Human-readable clinical alert notification for the therapist dashboard and VR HUD.
     */
    String getAlertMessage(CrisisContext context);
}
