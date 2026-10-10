package com.vrtherapy.dto;


public class ThoughtRecordDto {

    private String situation;

    private String automaticThought;

    private String emotion;
    private String cognitiveDistortion;

    private String alternativeThought;

    private int outcomeRating = 4;

    public ThoughtRecordDto() {
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getAutomaticThought() {
        return automaticThought;
    }

    public void setAutomaticThought(String automaticThought) {
        this.automaticThought = automaticThought;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getCognitiveDistortion() {
        return cognitiveDistortion;
    }

    public void setCognitiveDistortion(String cognitiveDistortion) {
        this.cognitiveDistortion = cognitiveDistortion;
    }

    public String getAlternativeThought() {
        return alternativeThought;
    }

    public void setAlternativeThought(String alternativeThought) {
        this.alternativeThought = alternativeThought;
    }

    public int getOutcomeRating() {
        return outcomeRating;
    }

    public void setOutcomeRating(int outcomeRating) {
        this.outcomeRating = outcomeRating;
    }
}
