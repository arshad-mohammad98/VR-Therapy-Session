package com.vrtherapy.strategy;

import com.vrtherapy.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SuggestionStrategyTest {

    private TherapySession session;

    @BeforeEach
    void setUp() {
        session = new TherapySession(
                "Exposure 1",
                ConditionType.PHOBIA,
                EnvironmentType.HEIGHTS_EXPOSURE,
                30,
                4,
                true,
                false,
                "Notes",
                1L
        );
        session.setId(10L);
    }

    @Test
    @DisplayName("HighStressSuggestionStrategy applies when post stress exceeds pre stress")
    void testHighStressStrategyAppliesWhenPostStressExceedsPreStress() {
        SessionResult result = new SessionResult(10L, 2L, 5, 8, 1800, 90, 110);
        SuggestionContext context = new SuggestionContext(session, List.of(result), List.of());

        HighStressSuggestionStrategy strategy = new HighStressSuggestionStrategy();
        assertTrue(strategy.applies(context));

        Optional<SessionAdjustmentSuggestion> suggestion = strategy.evaluate(context);
        assertTrue(suggestion.isPresent());
        assertEquals("REDUCE_INTENSITY", suggestion.get().getActionType());
        assertEquals(3, suggestion.get().getTargetIntensity()); // stepped down from 4 to 3
        assertTrue(suggestion.get().getTargetGuidedBreathing());
    }

    @Test
    @DisplayName("LowRatingSuggestionStrategy applies when rating is below 3 stars and suggests shorter duration")
    void testLowRatingStrategyShortensDuration() {
        Feedback lowFeedback = new Feedback(1L, 2L, 10L, 2, 2, 4, "Too intense and felt dizzy.");
        SuggestionContext context = new SuggestionContext(session, List.of(), List.of(lowFeedback));

        LowRatingSuggestionStrategy strategy = new LowRatingSuggestionStrategy();
        assertTrue(strategy.applies(context));

        Optional<SessionAdjustmentSuggestion> suggestion = strategy.evaluate(context);
        assertTrue(suggestion.isPresent());
        assertEquals("SHORTEN_DURATION", suggestion.get().getActionType());
        assertEquals(20, suggestion.get().getTargetDurationMinutes()); // reduced from 30 to 20
    }

    @Test
    @DisplayName("HeartRateSpikeSuggestionStrategy triggers on high physiological heart rate")
    void testHeartRateSpikeStrategyTriggersOnElevatedBpm() {
        SessionResult result = new SessionResult(10L, 2L, 6, 6, 1800, 92, 115);
        SuggestionContext context = new SuggestionContext(session, List.of(result), List.of());

        HeartRateSpikeSuggestionStrategy strategy = new HeartRateSpikeSuggestionStrategy();
        assertTrue(strategy.applies(context));

        Optional<SessionAdjustmentSuggestion> suggestion = strategy.evaluate(context);
        assertTrue(suggestion.isPresent());
        assertTrue(suggestion.get().getTargetGuidedBreathing());
        assertEquals(3, suggestion.get().getTargetIntensity());
    }

    @Test
    @DisplayName("PositiveReinforcementSuggestionStrategy suggests challenge advancement on mastery")
    void testPositiveReinforcementStrategySuggestsAdvancement() {
        session.setIntensityLevel(2);
        SessionResult result = new SessionResult(10L, 2L, 8, 3, 1800, 68, 75);
        Feedback feedback = new Feedback(1L, 2L, 10L, 5, 5, 1, "I feel completely grounded and calm.");
        SuggestionContext context = new SuggestionContext(session, List.of(result), List.of(feedback));

        PositiveReinforcementSuggestionStrategy strategy = new PositiveReinforcementSuggestionStrategy();
        assertTrue(strategy.applies(context));

        Optional<SessionAdjustmentSuggestion> suggestion = strategy.evaluate(context);
        assertTrue(suggestion.isPresent());
        assertEquals("ADVANCE_INTENSITY", suggestion.get().getActionType());
        assertEquals(3, suggestion.get().getTargetIntensity()); // advanced from 2 to 3
    }

    @Test
    @DisplayName("SuggestionEngine prioritizes safety high-stress rule over positive reinforcement")
    void testSuggestionEnginePrioritizesSafetyRulesFirst() {
        List<SuggestionStrategy> strategies = List.of(
                new PositiveReinforcementSuggestionStrategy(),
                new HighStressSuggestionStrategy(),
                new LowRatingSuggestionStrategy(),
                new HeartRateSpikeSuggestionStrategy()
        );
        SuggestionEngine engine = new SuggestionEngine(strategies);

        // High stress scenario
        SessionResult result = new SessionResult(10L, 2L, 5, 9, 1800, 100, 120);
        Feedback feedback = new Feedback(1L, 2L, 10L, 1, 1, 5, "Awful");
        SuggestionContext context = new SuggestionContext(session, List.of(result), List.of(feedback));

        Optional<SessionAdjustmentSuggestion> best = engine.findBestSuggestion(context);
        assertTrue(best.isPresent());
        assertEquals("HighStressRegulationRule", best.get().getRuleName());
    }
}
