package com.vrtherapy.dto;

import com.vrtherapy.model.Feedback;
import com.vrtherapy.model.SessionAssignment;
import com.vrtherapy.model.SessionResult;
import com.vrtherapy.model.User;
import java.util.List;

/**
 * Analytical DTO packaging patient progress metrics, stress analytics, and historical session logs.
 */
public class PatientProgressDto {

    private User patient;
    private long totalAssigned;
    private long totalCompleted;
    private double completionRate; // 0 to 100%
    private double averagePreStress;
    private double averagePostStress;
    private double averageStressDelta;
    private double improvementPercentage; // % drop from pre to post
    private String overallTrend; // e.g. "MARKED_IMPROVEMENT", "STEADY_PROGRESS", "NEUTRAL", "ELEVATED_STRESS"
    private double averageRating;
    private double averageHeartRate;
    private List<SessionAssignment> assignments;
    private List<SessionResult> results;
    private List<Feedback> feedbacks;

    // Time-series data for Chart.js rendering
    private List<String> chartLabels;
    private List<Integer> preStressPoints;
    private List<Integer> postStressPoints;
    private List<Integer> heartRatePoints;

    public PatientProgressDto() {
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    public long getTotalAssigned() {
        return totalAssigned;
    }

    public void setTotalAssigned(long totalAssigned) {
        this.totalAssigned = totalAssigned;
    }

    public long getTotalCompleted() {
        return totalCompleted;
    }

    public void setTotalCompleted(long totalCompleted) {
        this.totalCompleted = totalCompleted;
    }

    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }

    public double getAveragePreStress() {
        return averagePreStress;
    }

    public void setAveragePreStress(double averagePreStress) {
        this.averagePreStress = averagePreStress;
    }

    public double getAveragePostStress() {
        return averagePostStress;
    }

    public void setAveragePostStress(double averagePostStress) {
        this.averagePostStress = averagePostStress;
    }

    public double getAverageStressDelta() {
        return averageStressDelta;
    }

    public void setAverageStressDelta(double averageStressDelta) {
        this.averageStressDelta = averageStressDelta;
    }

    public double getImprovementPercentage() {
        return improvementPercentage;
    }

    public void setImprovementPercentage(double improvementPercentage) {
        this.improvementPercentage = improvementPercentage;
    }

    public String getOverallTrend() {
        return overallTrend;
    }

    public void setOverallTrend(String overallTrend) {
        this.overallTrend = overallTrend;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public double getAverageHeartRate() {
        return averageHeartRate;
    }

    public void setAverageHeartRate(double averageHeartRate) {
        this.averageHeartRate = averageHeartRate;
    }

    public List<SessionAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<SessionAssignment> assignments) {
        this.assignments = assignments;
    }

    public List<SessionResult> getResults() {
        return results;
    }

    public void setResults(List<SessionResult> results) {
        this.results = results;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public List<String> getChartLabels() {
        return chartLabels;
    }

    public void setChartLabels(List<String> chartLabels) {
        this.chartLabels = chartLabels;
    }

    public List<Integer> getPreStressPoints() {
        return preStressPoints;
    }

    public void setPreStressPoints(List<Integer> preStressPoints) {
        this.preStressPoints = preStressPoints;
    }

    public List<Integer> getPostStressPoints() {
        return postStressPoints;
    }

    public void setPostStressPoints(List<Integer> postStressPoints) {
        this.postStressPoints = postStressPoints;
    }

    public List<Integer> getHeartRatePoints() {
        return heartRatePoints;
    }

    public void setHeartRatePoints(List<Integer> heartRatePoints) {
        this.heartRatePoints = heartRatePoints;
    }
}
