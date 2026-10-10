package com.vrtherapy.model;

/**
 * VR hardware and client display types supported by the telehealth platform.
 */
public enum DeviceType {
    HEADSET("Standalone VR Headset (Meta Quest / Pico / HTC Vive)"),
    BROWSER("Desktop WebXR Browser (Chrome / Edge / Firefox)"),
    MOBILE_VR("Mobile VR Viewer (Cardboard / Gear / Smartphone)");

    private final String displayName;

    DeviceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
