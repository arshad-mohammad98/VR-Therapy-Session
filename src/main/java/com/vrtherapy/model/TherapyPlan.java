package com.vrtherapy.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain entity representing a multi-week clinical therapy plan.
 */
public class TherapyPlan extends BaseEntity {

    private Long patientId;
    private Long therapistId;
    private String title;
    private ConditionType conditionType;
    private int totalWeeks = 4;
    private String status = "ACTIVE";
    private String goals;
    private List<ExposureLevel> exposureLevels = new ArrayList<>();

    public TherapyPlan() {
        super();
    }

    public TherapyPlan(Long patientId, Long therapistId, String title, ConditionType conditionType, int totalWeeks, String goals) {
        super();
        this.patientId = patientId;
        this.therapistId = therapistId;
        this.title = title;
        this.conditionType = conditionType;
        this.totalWeeks = totalWeeks;
        this.goals = goals;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getTherapistId() {
        return therapistId;
    }

    public void setTherapistId(Long therapistId) {
        this.therapistId = therapistId;
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

    public int getTotalWeeks() {
        return totalWeeks;
    }

    public void setTotalWeeks(int totalWeeks) {
        this.totalWeeks = totalWeeks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public List<ExposureLevel> getExposureLevels() {
        return exposureLevels;
    }

    public void setExposureLevels(List<ExposureLevel> exposureLevels) {
        this.exposureLevels = exposureLevels;
    }
}
