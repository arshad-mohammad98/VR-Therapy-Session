package com.vrtherapy.strategy;


public class StreakSevenBadgeStrategy implements BadgeStrategy {

    @Override
    public String getBadgeCode() {
        return "STREAK_7_DAY";
    }

    @Override
    public String getBadgeName() {
        return "7-Day Resilient Flow";
    }

    @Override
    public String getDescription() {
        return "Maintained active therapy participation across 7 scheduled milestones.";
    }

    @Override
    public String getIcon() {
        return "fa-fire";
    }

    @Override
    public boolean qualifies(BadgeContext context) {
        return context.getCurrentStreakDays() >= 7 || context.getTotalSessionsCompleted() >= 7;
    }
}
