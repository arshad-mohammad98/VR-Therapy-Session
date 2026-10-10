package com.vrtherapy.strategy;

/**
 * Value object encapsulating recommended clinical modifications to a VR therapy protocol.
 */
public class SessionAdjustmentSuggestion {

    private String ruleName;
    private String title;
    private String recommendationText;
    private String clinicalRationale;
    private String actionType; // REDUCE_INTENSITY, SHORTEN_DURATION, ENABLE_BREATHING, ADVANCE_INTENSITY
    private Integer targetIntensity;
    private Integer targetDurationMinutes;
    private Boolean targetGuidedBreathing;
    private Boolean targetBackgroundAudio;

    public SessionAdjustmentSuggestion() {
    }

    public SessionAdjustmentSuggestion(String recommendationText, Integer targetIntensity, Integer targetDurationMinutes) {
        this.recommendationText = recommendationText;
        this.title = "Manual Adjustment";
        this.targetIntensity = targetIntensity;
        this.targetDurationMinutes = targetDurationMinutes;
    }

    public SessionAdjustmentSuggestion(String ruleName, String title, String recommendationText,
                                       String clinicalRationale, String actionType,
                                       Integer targetIntensity, Integer targetDurationMinutes,
                                       Boolean targetGuidedBreathing, Boolean targetBackgroundAudio) {
        this.ruleName = ruleName;
        this.title = title;
        this.recommendationText = recommendationText;
        this.clinicalRationale = clinicalRationale;
        this.actionType = actionType;
        this.targetIntensity = targetIntensity;
        this.targetDurationMinutes = targetDurationMinutes;
        this.targetGuidedBreathing = targetGuidedBreathing;
        this.targetBackgroundAudio = targetBackgroundAudio;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRecommendationText() {
        return recommendationText;
    }

    public void setRecommendationText(String recommendationText) {
        this.recommendationText = recommendationText;
    }

    public String getClinicalRationale() {
        return clinicalRationale;
    }

    public void setClinicalRationale(String clinicalRationale) {
        this.clinicalRationale = clinicalRationale;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Integer getTargetIntensity() {
        return targetIntensity;
    }

    public void setTargetIntensity(Integer targetIntensity) {
        this.targetIntensity = targetIntensity;
    }

    public Integer getTargetDurationMinutes() {
        return targetDurationMinutes;
    }

    public void setTargetDurationMinutes(Integer targetDurationMinutes) {
        this.targetDurationMinutes = targetDurationMinutes;
    }

    public Boolean getTargetGuidedBreathing() {
        return targetGuidedBreathing;
    }

    public void setTargetGuidedBreathing(Boolean targetGuidedBreathing) {
        this.targetGuidedBreathing = targetGuidedBreathing;
    }

    public Boolean getTargetBackgroundAudio() {
        return targetBackgroundAudio;
    }

    public void setTargetBackgroundAudio(Boolean targetBackgroundAudio) {
        this.targetBackgroundAudio = targetBackgroundAudio;
    }

    public Integer getNewIntensity() {
        return targetIntensity;
    }

    public Integer getNewDurationMinutes() {
        return targetDurationMinutes;
    }

    public String getSuggestedAction() {
        return actionType != null ? actionType : recommendationText;
    }
}
