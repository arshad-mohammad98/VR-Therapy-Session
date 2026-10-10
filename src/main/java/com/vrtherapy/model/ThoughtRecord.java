package com.vrtherapy.model;

/**
 * Domain entity representing cognitive behavioral therapy (CBT) thought monitoring worksheets.
 */
public class ThoughtRecord extends BaseEntity {

    private Long patientId;
    private String situation;
    private String automaticThought;
    private String cognitiveDistortion;
    private String rationalResponse;
    private int outcomeBelief;

    public ThoughtRecord() {
        super();
    }

    public ThoughtRecord(Long patientId, String situation, String automaticThought,
                         String cognitiveDistortion, String rationalResponse, int outcomeBelief) {
        super();
        this.patientId = patientId;
        this.situation = situation;
        this.automaticThought = automaticThought;
        this.cognitiveDistortion = cognitiveDistortion;
        this.rationalResponse = rationalResponse;
        this.outcomeBelief = outcomeBelief;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
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

    public String getCognitiveDistortion() {
        return cognitiveDistortion;
    }

    public void setCognitiveDistortion(String cognitiveDistortion) {
        this.cognitiveDistortion = cognitiveDistortion;
    }

    public String getRationalResponse() {
        return rationalResponse;
    }

    public void setRationalResponse(String rationalResponse) {
        this.rationalResponse = rationalResponse;
    }

    public int getOutcomeBelief() {
        return outcomeBelief;
    }

    public void setOutcomeBelief(int outcomeBelief) {
        this.outcomeBelief = outcomeBelief;
    }
}
