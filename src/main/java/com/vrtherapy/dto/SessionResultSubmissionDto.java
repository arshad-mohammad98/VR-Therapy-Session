package com.vrtherapy.dto;


/**
 * REST payload submitted when a patient completes a WebXR session.
 */
public class SessionResultSubmissionDto {

    private Long assignmentId;

    private Integer durationMinutes;

    private Integer preStressRating;

    private Integer postStressRating;

    private Integer averageHeartRate;

    private Integer peakHeartRate;

    private Long sessionId;
    private Integer durationSeconds;
    private boolean emergencyStopped;
    private String emergencyStopReason;
    private String clinicalNotes;

    public SessionResultSubmissionDto() {
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getDurationSeconds() {
        return durationSeconds != null ? durationSeconds : (durationMinutes != null ? durationMinutes * 60 : 0);
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public boolean isEmergencyStopped() {
        return emergencyStopped;
    }

    public void setEmergencyStopped(boolean emergencyStopped) {
        this.emergencyStopped = emergencyStopped;
    }

    public String getEmergencyStopReason() {
        return emergencyStopReason;
    }

    public void setEmergencyStopReason(String emergencyStopReason) {
        this.emergencyStopReason = emergencyStopReason;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Integer getDurationMinutes() {
        return durationMinutes != null ? durationMinutes : (durationSeconds != null ? durationSeconds / 60 : 0);
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getPreStressRating() {
        return preStressRating;
    }

    public void setPreStressRating(Integer preStressRating) {
        this.preStressRating = preStressRating;
    }

    public Integer getPostStressRating() {
        return postStressRating;
    }

    public void setPostStressRating(Integer postStressRating) {
        this.postStressRating = postStressRating;
    }

    public Integer getAverageHeartRate() {
        return averageHeartRate;
    }

    public void setAverageHeartRate(Integer averageHeartRate) {
        this.averageHeartRate = averageHeartRate;
    }

    public Integer getPeakHeartRate() {
        return peakHeartRate;
    }

    public void setPeakHeartRate(Integer peakHeartRate) {
        this.peakHeartRate = peakHeartRate;
    }

    public String getClinicalNotes() {
        return clinicalNotes;
    }

    public void setClinicalNotes(String clinicalNotes) {
        this.clinicalNotes = clinicalNotes;
    }
}
