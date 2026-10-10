package com.vrtherapy.strategy;

import com.vrtherapy.model.Badge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Strategy pattern orchestrator determining candidate badges for patient clinical progression.
 */
public class BadgeEngine {

    private final List<BadgeStrategy> strategies;

    public BadgeEngine() {
        this.strategies = new ArrayList<>();
        this.strategies.add(new FirstSessionBadgeStrategy());
        this.strategies.add(new CalmZenMasterBadgeStrategy());
        this.strategies.add(new StreakSevenBadgeStrategy());
        this.strategies.add(new LevelThreeMasterBadgeStrategy());
    }

    public BadgeEngine(List<BadgeStrategy> strategies) {
        this.strategies = strategies != null ? strategies : Collections.emptyList();
    }

    /**
     * Determines which badges the patient qualifies for that they haven't already unlocked.
     */
    public List<Badge> evaluateNewBadges(BadgeContext context, Set<String> existingBadgeCodes) {
        List<Badge> newBadges = new ArrayList<>();
        for (BadgeStrategy strategy : strategies) {
            if (!existingBadgeCodes.contains(strategy.getBadgeCode()) && strategy.qualifies(context)) {
                newBadges.add(strategy.createBadge(context));
            }
        }
        return newBadges;
    }

    public List<BadgeStrategy> getStrategies() {
        return Collections.unmodifiableList(strategies);
    }
}
