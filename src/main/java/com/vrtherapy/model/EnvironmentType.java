package com.vrtherapy.model;

/**
 * Procedural WebXR environments available in the virtual therapy platform.
 */
public enum EnvironmentType {
    CALM_BEACH("Calm Beach", "Tropical shoreline with gentle wave audio, warm daylight, and coastal fauna."),
    FOREST_WALK("Forest Walk", "Lush woodland with sunbeams filtering through leaves, gentle bird song, and rustling winds."),
    HEIGHTS_EXPOSURE("Heights Exposure", "High-altitude observation deck for graduated acrophobia exposure."),
    SOCIAL_GATHERING("Social Gathering", "Cozy terrace café with simulated gentle social ambient conversation."),
    BREATHING_ROOM("Breathing Room", "Minimalist biofeedback sanctuary with pulsating rhythmic light spheres."),
    NIGHT_SKY("Night Sky Sleep Room", "Tranquil starlit meadow with celestial nebula, shooting stars, and soothing night ambient tones for sleep therapy."),
    NIGHT_SKY_SLEEP_ROOM("Night Sky Sleep Room", "Tranquil starlit meadow with celestial nebula, shooting stars, and soothing night ambient tones for sleep therapy.");

    private final String displayName;
    private final String description;

    EnvironmentType(String displayName, String description) {
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
