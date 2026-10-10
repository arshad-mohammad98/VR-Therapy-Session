package com.vrtherapy.strategy;


public class FirstSessionBadgeStrategy implements BadgeStrategy {

    @Override
    public String getBadgeCode() {
        return "FIRST_SESSION";
    }

    @Override
    public String getBadgeName() {
        return "Virtual Pioneer";
    }

    @Override
    public String getDescription() {
        return "Completed your very first immersive VR therapy exposure session.";
    }

    @Override
    public String getIcon() {
        return "fa-vr-cardboard";
    }

    @Override
    public boolean qualifies(BadgeContext context) {
        return context.getTotalSessionsCompleted() >= 1;
    }
}
