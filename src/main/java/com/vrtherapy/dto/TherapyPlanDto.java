package com.vrtherapy.dto;

import com.vrtherapy.model.ConditionType;

public class TherapyPlanDto {

    private Long patientId;

    private String title;

    private String clinicalGoal;

    private ConditionType targetCondition = ConditionType.PHOBIA;

    private int totalWeeks = 6;

    public TherapyPlanDto() {
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClinicalGoal() {
        return clinicalGoal;
    }

    public void setClinicalGoal(String clinicalGoal) {
        this.clinicalGoal = clinicalGoal;
    }

    public ConditionType getTargetCondition() {
        return targetCondition;
    }

    public void setTargetCondition(ConditionType targetCondition) {
        this.targetCondition = targetCondition;
    }

    public int getTotalWeeks() {
        return totalWeeks;
    }

    public void setTotalWeeks(int totalWeeks) {
        this.totalWeeks = totalWeeks;
    }
}
