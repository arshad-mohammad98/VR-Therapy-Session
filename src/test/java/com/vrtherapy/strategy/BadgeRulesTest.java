package com.vrtherapy.strategy;

import com.vrtherapy.model.Badge;
import com.vrtherapy.model.Role;
import com.vrtherapy.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BadgeRulesTest {

    private FirstSessionBadgeStrategy firstSessionStrategy;
    private StreakSevenBadgeStrategy streakStrategy;
    private LevelThreeMasterBadgeStrategy levelThreeStrategy;
    private CalmZenMasterBadgeStrategy zenStrategy;
    private BadgeEngine badgeEngine;
    private User samplePatient;

    @BeforeEach
    void setUp() {
        firstSessionStrategy = new FirstSessionBadgeStrategy();
        streakStrategy = new StreakSevenBadgeStrategy();
        levelThreeStrategy = new LevelThreeMasterBadgeStrategy();
        zenStrategy = new CalmZenMasterBadgeStrategy();

        badgeEngine = new BadgeEngine(List.of(firstSessionStrategy, streakStrategy, levelThreeStrategy, zenStrategy));
        samplePatient = new com.vrtherapy.model.Patient("patient1", "pass", "Sarah", "sarah@example.com", true);
    }

    @Test
    @DisplayName("Badge: First Session awarded when completed sessions >= 1")
    void testFirstSessionQualification() {
        BadgeContext context = new BadgeContext(samplePatient, 1, 1, 0, 10.0);
        assertTrue(firstSessionStrategy.qualifies(context));

        BadgeContext zeroSessions = new BadgeContext(samplePatient, 0, 0, 0, 0.0);
        assertFalse(firstSessionStrategy.qualifies(zeroSessions));
    }

    @Test
    @DisplayName("Badge: 7-Day Streak awarded when streak or completed sessions >= 7")
    void testStreakSevenQualification() {
        BadgeContext qualifying = new BadgeContext(samplePatient, 7, 7, 1, 20.0);
        assertTrue(streakStrategy.qualifies(qualifying));

        BadgeContext nonQualifying = new BadgeContext(samplePatient, 4, 4, 1, 20.0);
        assertFalse(streakStrategy.qualifies(nonQualifying));
    }

    @Test
    @DisplayName("Badge: Level 3 Cleared awarded when hierarchy levels cleared >= 3")
    void testLevelThreeMasterQualification() {
        BadgeContext cleared = new BadgeContext(samplePatient, 5, 5, 3, 30.0);
        assertTrue(levelThreeStrategy.qualifies(cleared));

        BadgeContext notYet = new BadgeContext(samplePatient, 5, 5, 2, 30.0);
        assertFalse(levelThreeStrategy.qualifies(notYet));
    }

    @Test
    @DisplayName("Badge: Zen Master awarded when subjective stress improvement >= 50%")
    void testZenMasterQualification() {
        BadgeContext highImprovement = new BadgeContext(samplePatient, 3, 3, 1, 62.5);
        assertTrue(zenStrategy.qualifies(highImprovement));

        BadgeContext lowImprovement = new BadgeContext(samplePatient, 3, 3, 1, 35.0);
        assertFalse(zenStrategy.qualifies(lowImprovement));
    }

    @Test
    @DisplayName("Badge Engine: awards only new unearned badges without duplication")
    void testBadgeEngineDedup() {
        BadgeContext fullContext = new BadgeContext(samplePatient, 8, 8, 4, 60.0);

        // Suppose user already has FIRST_SESSION
        Set<String> alreadyOwned = Set.of("FIRST_SESSION");
        List<Badge> newBadges = badgeEngine.evaluateNewBadges(fullContext, alreadyOwned);

        assertEquals(3, newBadges.size());
        assertTrue(newBadges.stream().noneMatch(b -> b.getBadgeCode().equals("FIRST_SESSION")));
        assertTrue(newBadges.stream().anyMatch(b -> b.getBadgeCode().equals("STREAK_7_DAY")));
        assertTrue(newBadges.stream().anyMatch(b -> b.getBadgeCode().equals("LEVEL_3_CLEARED")));
        assertTrue(newBadges.stream().anyMatch(b -> b.getBadgeCode().equals("ZEN_MASTER")));
    }
}
