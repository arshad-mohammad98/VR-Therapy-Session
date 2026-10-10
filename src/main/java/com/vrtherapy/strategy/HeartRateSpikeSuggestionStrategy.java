package com.vrtherapy.strategy;

import com.vrtherapy.model.TherapySession;
import java.util.Optional;

/**
 * Strategy triggered by autonomic sympathetic arousal spikes detected in simulated heart-rate telemetry.
 */
public class HeartRateSpikeSuggestionStrategy implements SuggestionStrategy {

    @Override
    public boolean applies(SuggestionContext context) {
        if (!context.hasData() || context.getResults().isEmpty()) {
            return false;
        }
        return context.getPeakHeartRateObserved() >= 105 || context.getAverageHeartRate() >= 90.0;
    }

    @Override
    public Optional<SessionAdjustmentSuggestion> evaluate(SuggestionContext context) {
        if (!applies(context)) {
            return Optional.empty();
        }

        TherapySession session = context.getSession();
        int currentIntensity = session.getIntensityLevel();
        int adjustedIntensity = Math.max(1, currentIntensity - 1);

        SessionAdjustmentSuggestion suggestion = new SessionAdjustmentSuggestion(
                "AutonomicSympatheticRegulationRule",
                "Enable Biofeedback Guided Breathing & Reduce Stimulation",
                String.format("Biometric telemetry reveals heart rate spikes (Peak: %d bpm, Avg: %.1f bpm). "
                                + "Recommend enabling guided breathing pacing sphere and stepping down stimulus intensity to %d.",
                        context.getPeakHeartRateObserved(), context.getAverageHeartRate(), adjustedIntensity),
                "Elevated cardiac markers correlate with acute flight-or-fight response; vagal stimulation via paced breathing restores autonomic equilibrium.",
                "ENABLE_BREATHING",
                adjustedIntensity,
                session.getDurationMinutes(),
                true,
                true
        );

        return Optional.of(suggestion);
    }

    @Override
    public int getPriority() {
        return 15;
    }
}
