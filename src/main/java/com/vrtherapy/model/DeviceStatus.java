package com.vrtherapy.model;

/**
 * Real-time operational connection status of a patient's VR hardware.
 */
public enum DeviceStatus {
    PAIRING("Pairing Pending", "Device has an active 6-digit pairing code awaiting client confirmation"),
    CONNECTED("Connected", "Device is actively paired with a fresh telemetry heartbeat"),
    DISCONNECTED("Disconnected", "Device connection dropped or heartbeat timed out"),
    BLOCKED("Blocked / Revoked", "Device blocked by clinical administrator for safety or maintenance");

    private final String displayName;
    private final String description;

    DeviceStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
