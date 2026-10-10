package com.vrtherapy.strategy;

import com.vrtherapy.model.User;

/**
 * Context summarizing patient engagement and clinical milestones for gamified badge evaluation.
 */
public class BadgeContext {

    private final User patient;
    private final int totalSessionsCompleted;
    private final int currentStreakDays;
    private final int hierarchyLevelsCleared;
    private final double bestImprovementPct;

    public BadgeContext(User patient, int totalSessionsCompleted, int currentStreakDays, int hierarchyLevelsCleared, double bestImprovementPct) {
        this.patient = patient;
        this.totalSessionsCompleted = totalSessionsCompleted;
        this.currentStreakDays = currentStreakDays;
        this.hierarchyLevelsCleared = hierarchyLevelsCleared;
        this.bestImprovementPct = bestImprovementPct;
    }

    public User getPatient() {
        return patient;
    }

    public int getTotalSessionsCompleted() {
        return totalSessionsCompleted;
    }

    public int getCurrentStreakDays() {
        return currentStreakDays;
    }

    public int getHierarchyLevelsCleared() {
        return hierarchyLevelsCleared;
    }

    public double getBestImprovementPct() {
        return bestImprovementPct;
    }
}
