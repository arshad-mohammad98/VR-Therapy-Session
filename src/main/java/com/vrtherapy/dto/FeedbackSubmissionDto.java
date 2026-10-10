package com.vrtherapy.dto;


/**
 * Data transfer object for patient subjective feedback submissions.
 */
public class FeedbackSubmissionDto {

    private Long sessionId;
    private Long assignmentId;
    private Integer rating = 5;
    private Integer comfortScore = 4;
    private Integer nauseaScore = 1;
    private String comments;

    public FeedbackSubmissionDto() {
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getComfortScore() {
        return comfortScore;
    }

    public void setComfortScore(Integer comfortScore) {
        this.comfortScore = comfortScore;
    }

    public Integer getComfortLevel() {
        return comfortScore;
    }

    public void setComfortLevel(Integer comfortLevel) {
        this.comfortScore = comfortLevel;
    }

    public Integer getNauseaScore() {
        return nauseaScore;
    }

    public void setNauseaScore(Integer nauseaScore) {
        this.nauseaScore = nauseaScore;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
