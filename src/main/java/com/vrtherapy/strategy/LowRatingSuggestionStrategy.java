package com.vrtherapy.strategy;

import com.vrtherapy.model.TherapySession;
import java.util.Optional;

/**
 * Strategy triggered when subjective patient feedback scores or comfort levels are sub-optimal (e.g. rating < 3.0).
 */
public class LowRatingSuggestionStrategy implements SuggestionStrategy {

    @Override
    public boolean applies(SuggestionContext context) {
        if (!context.hasData() || context.getFeedbacks().isEmpty()) {
            return false;
        }
        return context.getAverageRating() < 3.0 || context.getAverageComfortLevel() < 3.0;
    }

    @Override
    public Optional<SessionAdjustmentSuggestion> evaluate(SuggestionContext context) {
        if (!applies(context)) {
            return Optional.empty();
        }

        TherapySession session = context.getSession();
        int currentDuration = session.getDurationMinutes();
        int newDuration = Math.max(10, currentDuration - 10);

        SessionAdjustmentSuggestion suggestion = new SessionAdjustmentSuggestion(
                "LowRatingComfortOptimizationRule",
                "Shorten Session Duration to Mitigate Fatigue",
                String.format("Patient reported low satisfaction / comfort (Avg Rating: %.1f/5, Avg Comfort: %.1f/5). "
                                + "Recommend shortening immersion duration from %d minutes to %d minutes to prevent VR cognitive fatigue and cybersickness.",
                        context.getAverageRating(), context.getAverageComfortLevel(), currentDuration, newDuration),
                "Prolonged exposure when comfort ratings are compromised leads to treatment avoidance and simulation discomfort.",
                "SHORTEN_DURATION",
                session.getIntensityLevel(),
                newDuration,
                session.isGuidedBreathingEnabled(),
                session.isBackgroundAudioEnabled()
        );

        return Optional.of(suggestion);
    }

    @Override
    public int getPriority() {
        return 20;
    }
}
