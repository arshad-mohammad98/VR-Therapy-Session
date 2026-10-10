package com.vrtherapy.model;

import java.util.Set;

/**
 * State machine representing the clinical lifecycle of a live remote-guided VR therapy session.
 */
public enum LiveSessionState {
    SCHEDULED("Scheduled", "Session room initialized by therapist"),
    WAITING("Waiting for Patient", "Therapist in room; waiting for headset or browser join"),
    ACTIVE("Active / In Progress", "Patient connected in VR; telemetry streaming live"),
    PAUSED("Paused", "Session temporarily suspended by therapist or safety threshold"),
    COMPLETED("Completed", "Session concluded successfully with summary recorded"),
    EMERGENCY_STOPPED("Emergency Stopped", "Immediate clinical halt triggered by patient panic button or therapist override");

    private final String displayName;
    private final String description;

    LiveSessionState(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Enforces valid state machine transitions according to clinical safety protocols.
     *
     * @param targetState The proposed next state
     * @return true if the transition is allowed, false otherwise
     */
    public boolean canTransitionTo(LiveSessionState targetState) {
        if (targetState == null) {
            return false;
        }
        if (this == targetState) {
            return true; // No-op transition
        }

        // Terminal states cannot transition to anything
        if (this == COMPLETED || this == EMERGENCY_STOPPED) {
            return false;
        }

        // Emergency Stop can be triggered from ANY active, paused, waiting, or scheduled state
        if (targetState == EMERGENCY_STOPPED) {
            return true;
        }

        return switch (this) {
            case SCHEDULED -> targetState == WAITING || targetState == ACTIVE;
            case WAITING -> targetState == ACTIVE || targetState == PAUSED;
            case ACTIVE -> targetState == PAUSED || targetState == COMPLETED;
            case PAUSED -> targetState == ACTIVE || targetState == COMPLETED;
            case COMPLETED, EMERGENCY_STOPPED -> false;
        };
    }
}
