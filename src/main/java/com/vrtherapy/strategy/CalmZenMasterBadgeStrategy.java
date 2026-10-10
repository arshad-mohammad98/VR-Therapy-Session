package com.vrtherapy.strategy;


public class CalmZenMasterBadgeStrategy implements BadgeStrategy {

    @Override
    public String getBadgeCode() {
        return "ZEN_MASTER";
    }

    @Override
    public String getBadgeName() {
        return "Zen Recovery Master";
    }

    @Override
    public String getDescription() {
        return "Achieved over 50% subjective stress reduction in a single therapeutic session.";
    }

    @Override
    public String getIcon() {
        return "fa-spa";
    }

    @Override
    public boolean qualifies(BadgeContext context) {
        return context.getBestImprovementPct() >= 50.0;
    }
}
