package com.vrtherapy.strategy;

import com.vrtherapy.model.TherapySession;
import java.util.Optional;

/**
 * Strategy triggered when patient demonstrates marked clinical adaptation and relaxation gains.
 */
public class PositiveReinforcementSuggestionStrategy implements SuggestionStrategy {

    @Override
    public boolean applies(SuggestionContext context) {
        if (!context.hasData() || context.getResults().isEmpty()) {
            return false;
        }
        double stressDelta = context.getAveragePreStress() - context.getAveragePostStress();
        return stressDelta >= 2.0 && context.getAveragePostStress() <= 4.0 && context.getAverageRating() >= 4.0;
    }

    @Override
    public Optional<SessionAdjustmentSuggestion> evaluate(SuggestionContext context) {
        if (!applies(context)) {
            return Optional.empty();
        }

        TherapySession session = context.getSession();
        int currentIntensity = session.getIntensityLevel();
        int targetIntensity = Math.min(5, currentIntensity + (currentIntensity < 5 ? 1 : 0));

        String recommendation = currentIntensity < 5
                ? String.format("Patient exhibits strong therapeutic adaptation (Stress dropped by %.1f pts, Rating: %.1f). "
                + "Therapist may safely advance challenge intensity to %d to promote further habituation.",
                context.getAveragePreStress() - context.getAveragePostStress(), context.getAverageRating(), targetIntensity)
                : "Patient has reached mastery of this protocol. Maintain current therapeutic parameters or prepare transition to maintenance.";

        SessionAdjustmentSuggestion suggestion = new SessionAdjustmentSuggestion(
                "TherapeuticMasteryRule",
                "Graduated Progress: Patient Ready for Challenge Advancement",
                recommendation,
                "Consistently low post-session anxiety and high comfort ratings confirm successful emotional habituation.",
                currentIntensity < 5 ? "ADVANCE_INTENSITY" : "MAINTAIN_CURRENT",
                targetIntensity,
                session.getDurationMinutes(),
                session.isGuidedBreathingEnabled(),
                session.isBackgroundAudioEnabled()
        );

        return Optional.of(suggestion);
    }

    @Override
    public int getPriority() {
        return 50; // Evaluated after safety threshold rules
    }
}
