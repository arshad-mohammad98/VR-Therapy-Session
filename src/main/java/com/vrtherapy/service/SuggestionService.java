package com.vrtherapy.service;

import com.vrtherapy.model.TherapySession;
import com.vrtherapy.strategy.SessionAdjustmentSuggestion;
import java.util.List;
import java.util.Optional;

/**
 * Service contract bridging feedback analysis with the Strategy-based suggestion engine.
 */
public interface SuggestionService {

    /**
     * Evaluates all telemetry and feedback for a session and returns prioritized clinical suggestions.
     */
    List<SessionAdjustmentSuggestion> generateSuggestionsForSession(Long sessionId);

    /**
     * Evaluates the single highest-priority suggestion for a session.
     */
    Optional<SessionAdjustmentSuggestion> getPrimarySuggestionForSession(Long sessionId);

    /**
     * Evaluates suggestions for a specific session and historical dataset directly.
     */
    List<SessionAdjustmentSuggestion> evaluateSession(TherapySession session);
}
