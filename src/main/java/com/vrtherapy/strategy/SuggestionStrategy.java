package com.vrtherapy.strategy;

import java.util.Optional;

/**
 * Strategy pattern interface for clinical rule-based therapy adjustment engines.
 */
public interface SuggestionStrategy {

    /**
     * Determines whether the given context satisfies preconditions for this strategy.
     */
    boolean applies(SuggestionContext context);

    /**
     * Produces a concrete adjustment suggestion based on clinical algorithms.
     */
    Optional<SessionAdjustmentSuggestion> evaluate(SuggestionContext context);

    /**
     * Priority order for sorting strategies (lower number = evaluated first).
     */
    int getPriority();
}
