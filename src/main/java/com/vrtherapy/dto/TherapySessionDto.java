package com.vrtherapy.dto;

import com.vrtherapy.model.ConditionType;
import com.vrtherapy.model.EnvironmentType;

/**
 * Data transfer object for Therapist VR Session Design forms.
 */
public class TherapySessionDto {

    private Long id;
    private String title;
    private ConditionType conditionType = ConditionType.ANXIETY;
    private EnvironmentType environmentType = EnvironmentType.CALM_BEACH;
    private Integer durationMinutes = 20;
    private Integer intensityLevel = 2;
    private boolean backgroundAudioEnabled = true;
    private boolean guidedBreathingEnabled = true;
    private String clinicalNotes;

    public TherapySessionDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setConditionType(String condition) {
        if (condition != null && !condition.isBlank()) {
            try {
                this.conditionType = ConditionType.valueOf(condition.toUpperCase().replace(" ", "_"));
            } catch (IllegalArgumentException e) {
                this.conditionType = ConditionType.ANXIETY;
            }
        }
    }

    public String getConditionTypeString() {
        return conditionType != null ? conditionType.name() : "ANXIETY";
    }

    public EnvironmentType getEnvironmentType() {
        return environmentType;
    }

    public void setEnvironmentType(EnvironmentType environmentType) {
        this.environmentType = environmentType;
    }

    public String getVrEnvironment() {
        return environmentType != null ? environmentType.name() : "CALM_BEACH";
    }

    public void setVrEnvironment(String vrEnv) {
        if (vrEnv != null && !vrEnv.isBlank()) {
            try {
                this.environmentType = EnvironmentType.valueOf(vrEnv.toUpperCase().replace(" ", "_"));
            } catch (IllegalArgumentException e) {
                this.environmentType = EnvironmentType.CALM_BEACH;
            }
        }
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getDefaultDurationMinutes() {
        return durationMinutes;
    }

    public void setDefaultDurationMinutes(Integer defaultDurationMinutes) {
        this.durationMinutes = defaultDurationMinutes;
    }

    public Integer getIntensityLevel() {
        return intensityLevel;
    }

    public void setIntensityLevel(Integer intensityLevel) {
        this.intensityLevel = intensityLevel;
    }

    public boolean isBackgroundAudioEnabled() {
        return backgroundAudioEnabled;
    }

    public void setBackgroundAudioEnabled(boolean backgroundAudioEnabled) {
        this.backgroundAudioEnabled = backgroundAudioEnabled;
    }

    public boolean isAudioEnabled() {
        return backgroundAudioEnabled;
    }

    public void setAudioEnabled(boolean audioEnabled) {
        this.backgroundAudioEnabled = audioEnabled;
    }

    public boolean isGuidedBreathingEnabled() {
        return guidedBreathingEnabled;
    }

    public void setGuidedBreathingEnabled(boolean guidedBreathingEnabled) {
        this.guidedBreathingEnabled = guidedBreathingEnabled;
    }

    public String getClinicalNotes() {
        return clinicalNotes;
    }

    public void setClinicalNotes(String clinicalNotes) {
        this.clinicalNotes = clinicalNotes;
    }

    public String getTherapistNotes() {
        return clinicalNotes;
    }

    public void setTherapistNotes(String therapistNotes) {
        this.clinicalNotes = therapistNotes;
    }
}
