package com.vrtherapy.model;

/**
 * Domain entity representing a therapeutic VR session configuration.
 * Contains protocol settings such as target condition, 3D environment, duration, and intensity.
 */
public class TherapySession extends BaseEntity {

    private String title;
    private ConditionType conditionType;
    private EnvironmentType vrEnvironment;
    private int defaultDurationMinutes = 15;
    private int intensityLevel = 1;
    private boolean audioEnabled = true;
    private boolean guidedBreathingEnabled = true;
    private String therapistNotes;
    private String configJson;
    private Long createdBy;

    public TherapySession() {
        super();
    }

    public TherapySession(String title, ConditionType conditionType, EnvironmentType vrEnvironment,
                          int defaultDurationMinutes, int intensityLevel, boolean audioEnabled,
                          boolean guidedBreathingEnabled, String therapistNotes, Long createdBy) {
        super();
        this.title = title;
        this.conditionType = conditionType;
        this.vrEnvironment = vrEnvironment;
        this.defaultDurationMinutes = defaultDurationMinutes;
        this.intensityLevel = intensityLevel;
        this.audioEnabled = audioEnabled;
        this.guidedBreathingEnabled = guidedBreathingEnabled;
        this.therapistNotes = therapistNotes;
        this.createdBy = createdBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public EnvironmentType getVrEnvironment() {
        return vrEnvironment;
    }

    public void setVrEnvironment(EnvironmentType vrEnvironment) {
        this.vrEnvironment = vrEnvironment;
    }

    public int getDefaultDurationMinutes() {
        return defaultDurationMinutes;
    }

    public void setDefaultDurationMinutes(int defaultDurationMinutes) {
        this.defaultDurationMinutes = defaultDurationMinutes;
    }

    public int getDurationMinutes() {
        return defaultDurationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.defaultDurationMinutes = durationMinutes;
    }

    public int getIntensityLevel() {
        return intensityLevel;
    }

    public void setIntensityLevel(int intensityLevel) {
        this.intensityLevel = intensityLevel;
    }

    public boolean isAudioEnabled() {
        return audioEnabled;
    }

    public void setAudioEnabled(boolean audioEnabled) {
        this.audioEnabled = audioEnabled;
    }

    public boolean isBackgroundAudioEnabled() {
        return audioEnabled;
    }

    public void setBackgroundAudioEnabled(boolean backgroundAudioEnabled) {
        this.audioEnabled = backgroundAudioEnabled;
    }

    public boolean isGuidedBreathingEnabled() {
        return guidedBreathingEnabled;
    }

    public void setGuidedBreathingEnabled(boolean guidedBreathingEnabled) {
        this.guidedBreathingEnabled = guidedBreathingEnabled;
    }

    public String getTherapistNotes() {
        return therapistNotes;
    }

    public void setTherapistNotes(String therapistNotes) {
        this.therapistNotes = therapistNotes;
    }

    public String getConfigJson() {
        return configJson;
    }

    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}
