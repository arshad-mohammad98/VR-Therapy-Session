package com.vrtherapy.strategy;

import com.vrtherapy.model.TherapySession;
import java.util.Optional;

/**
 * Strategy triggered when post-session stress ratings are elevated or higher than baseline pre-session stress.
 */
public class HighStressSuggestionStrategy implements SuggestionStrategy {

    @Override
    public boolean applies(SuggestionContext context) {
        if (!context.hasData()) {
            return false;
        }
        // Triggered if post-stress exceeds pre-stress, or average post-stress is >= 7.0
        return context.getAveragePostStress() > context.getAveragePreStress()
                || context.getAveragePostStress() >= 7.0;
    }

    @Override
    public Optional<SessionAdjustmentSuggestion> evaluate(SuggestionContext context) {
        if (!applies(context)) {
            return Optional.empty();
        }

        TherapySession session = context.getSession();
        int currentIntensity = session.getIntensityLevel();
        int newIntensity = Math.max(1, currentIntensity - 1);

        SessionAdjustmentSuggestion suggestion = new SessionAdjustmentSuggestion(
                "HighStressRegulationRule",
                "Reduce Exposure Intensity & Activate Breathing Pacer",
                String.format("Patient exhibits elevated post-session distress (Avg Post: %.1f vs Avg Pre: %.1f). "
                                + "Recommend reducing immersion intensity from %d to %d and ensuring guided breathing is active.",
                        context.getAveragePostStress(), context.getAveragePreStress(), currentIntensity, newIntensity),
                "Prolonged hyper-arousal during exposure indicates the current sensory stimuli exceed the patient's autonomic tolerance zone.",
                "REDUCE_INTENSITY",
                newIntensity,
                session.getDurationMinutes(),
                true, // Ensure guided breathing is active
                true  // Ensure background audio is active
        );

        return Optional.of(suggestion);
    }

    @Override
    public int getPriority() {
        return 10; // High clinical safety priority
    }
}
