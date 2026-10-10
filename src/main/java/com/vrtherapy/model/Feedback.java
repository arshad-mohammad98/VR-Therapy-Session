package com.vrtherapy.model;

/**
 * Domain entity representing patient subjective feedback on comfort, simulation sickness, and satisfaction.
 */
public class Feedback extends BaseEntity {

    private Long resultId;
    private Long patientId;
    private Long sessionId;
    private int rating;
    private int comfortScore;
    private int nauseaScore;
    private String comments;
    private String appliedAdjustment;

    public Feedback() {
        super();
    }

    public Feedback(Long resultId, Long patientId, Long sessionId, int rating, int comfortScore, int nauseaScore, String comments) {
        super();
        this.resultId = resultId;
        this.patientId = patientId;
        this.sessionId = sessionId;
        this.rating = rating;
        this.comfortScore = comfortScore;
        this.nauseaScore = nauseaScore;
        this.comments = comments;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getComfortScore() {
        return comfortScore;
    }

    public int getComfortLevel() {
        return comfortScore;
    }

    public void setComfortScore(int comfortScore) {
        this.comfortScore = comfortScore;
    }

    public int getNauseaScore() {
        return nauseaScore;
    }

    public void setNauseaScore(int nauseaScore) {
        this.nauseaScore = nauseaScore;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getAppliedAdjustment() {
        return appliedAdjustment;
    }

    public void setAppliedAdjustment(String appliedAdjustment) {
        this.appliedAdjustment = appliedAdjustment;
    }
}
