package com.vrtherapy.strategy;


public class LevelThreeMasterBadgeStrategy implements BadgeStrategy {

    @Override
    public String getBadgeCode() {
        return "LEVEL_3_CLEARED";
    }

    @Override
    public String getBadgeName() {
        return "Fear Conqueror - Level 3";
    }

    @Override
    public String getDescription() {
        return "Successfully reduced distress and unlocked Level 3 on your Exposure Hierarchy ladder.";
    }

    @Override
    public String getIcon() {
        return "fa-mountain";
    }

    @Override
    public boolean qualifies(BadgeContext context) {
        return context.getHierarchyLevelsCleared() >= 3;
    }
}
