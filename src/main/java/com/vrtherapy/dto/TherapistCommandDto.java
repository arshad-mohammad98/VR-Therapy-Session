package com.vrtherapy.dto;

import com.vrtherapy.model.EnvironmentType;

public class TherapistCommandDto {

    private String commandType; // SET_INTENSITY, SWITCH_ENVIRONMENT, TOGGLE_BREATHING, VR_PROMPT, PAUSE, RESUME, EMERGENCY_STOP
    private EnvironmentType environment;
    private Integer intensity;
    private Boolean guidedBreathing;
    private String promptText;
    private String reason;

    public TherapistCommandDto() {
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public EnvironmentType getEnvironment() {
        return environment;
    }

    public void setEnvironment(EnvironmentType environment) {
        this.environment = environment;
    }

    public Integer getIntensity() {
        return intensity;
    }

    public void setIntensity(Integer intensity) {
        this.intensity = intensity;
    }

    public Boolean getGuidedBreathing() {
        return guidedBreathing;
    }

    public void setGuidedBreathing(Boolean guidedBreathing) {
        this.guidedBreathing = guidedBreathing;
    }

    public String getPromptText() {
        return promptText;
    }

    public void setPromptText(String promptText) {
        this.promptText = promptText;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
