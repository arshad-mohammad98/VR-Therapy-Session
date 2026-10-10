package com.vrtherapy.strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Orchestrator implementing the Strategy pattern for clinical decision support.
 * Demonstrates Collections, Generics, Stream API, and Strategy Pattern.
 */
public class SuggestionEngine {

    private final List<SuggestionStrategy> strategies;

    public SuggestionEngine() {
        this.strategies = new ArrayList<>();
        this.strategies.add(new HighStressSuggestionStrategy());
        this.strategies.add(new HeartRateSpikeSuggestionStrategy());
        this.strategies.add(new LowRatingSuggestionStrategy());
        this.strategies.add(new PositiveReinforcementSuggestionStrategy());
        this.strategies.sort(Comparator.comparingInt(SuggestionStrategy::getPriority));
    }

    public SuggestionEngine(List<SuggestionStrategy> strategies) {
        this.strategies = strategies.stream()
                .sorted(Comparator.comparingInt(SuggestionStrategy::getPriority))
                .collect(Collectors.toList());
    }

    /**
     * Finds the highest-priority clinical suggestion applicable to the given session context.
     */
    public Optional<SessionAdjustmentSuggestion> findBestSuggestion(SuggestionContext context) {
        for (SuggestionStrategy strategy : strategies) {
            if (strategy.applies(context)) {
                Optional<SessionAdjustmentSuggestion> suggestion = strategy.evaluate(context);
                if (suggestion.isPresent()) {
                    return suggestion;
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Evaluates and returns all applicable suggestions.
     */
    public List<SessionAdjustmentSuggestion> findAllSuggestions(SuggestionContext context) {
        return strategies.stream()
                .filter(strategy -> strategy.applies(context))
                .map(strategy -> strategy.evaluate(context))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
