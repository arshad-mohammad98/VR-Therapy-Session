package com.vrtherapy.dto;

import com.vrtherapy.model.Feedback;
import com.vrtherapy.strategy.SessionAdjustmentSuggestion;
import java.util.List;

/**
 * Summary DTO aggregating patient subjective reviews, average rating, recurrent sentiments, and clinical suggestions.
 */
public class FeedbackSummaryDto {

    private Long sessionId;
    private String sessionTitle;
    private long totalFeedbacks;
    private double averageRating;
    private double averageComfortLevel;
    private List<String> commonComments;
    private List<Feedback> detailedFeedbacks;
    private SessionAdjustmentSuggestion primarySuggestion;
    private List<SessionAdjustmentSuggestion> allSuggestions;

    public FeedbackSummaryDto() {
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionTitle() {
        return sessionTitle;
    }

    public void setSessionTitle(String sessionTitle) {
        this.sessionTitle = sessionTitle;
    }

    public long getTotalFeedbacks() {
        return totalFeedbacks;
    }

    public void setTotalFeedbacks(long totalFeedbacks) {
        this.totalFeedbacks = totalFeedbacks;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public double getAverageComfortLevel() {
        return averageComfortLevel;
    }

    public void setAverageComfortLevel(double averageComfortLevel) {
        this.averageComfortLevel = averageComfortLevel;
    }

    public List<String> getCommonComments() {
        return commonComments;
    }

    public void setCommonComments(List<String> commonComments) {
        this.commonComments = commonComments;
    }

    public List<Feedback> getDetailedFeedbacks() {
        return detailedFeedbacks;
    }

    public void setDetailedFeedbacks(List<Feedback> detailedFeedbacks) {
        this.detailedFeedbacks = detailedFeedbacks;
    }

    public SessionAdjustmentSuggestion getPrimarySuggestion() {
        return primarySuggestion;
    }

    public void setPrimarySuggestion(SessionAdjustmentSuggestion primarySuggestion) {
        this.primarySuggestion = primarySuggestion;
    }

    public List<SessionAdjustmentSuggestion> getAllSuggestions() {
        return allSuggestions;
    }

    public void setAllSuggestions(List<SessionAdjustmentSuggestion> allSuggestions) {
        this.allSuggestions = allSuggestions;
    }
}
