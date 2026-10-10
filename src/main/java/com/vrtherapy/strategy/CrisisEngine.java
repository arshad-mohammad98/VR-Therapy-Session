package com.vrtherapy.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Strategy pattern orchestrator evaluating multi-factor crisis rules against active patient telemetry.
 */
public class CrisisEngine {

    private static final Logger log = LoggerFactory.getLogger(CrisisEngine.class);

    private final List<CrisisDetectionStrategy> strategies;

    public CrisisEngine() {
        this.strategies = new ArrayList<>();
        this.strategies.add(new HeartRateThresholdCrisisStrategy());
        this.strategies.add(new SubjectiveStressCrisisStrategy());
        this.strategies.add(new ProlongedElevationCrisisStrategy());
    }

    public CrisisEngine(List<CrisisDetectionStrategy> strategies) {
        this.strategies = strategies != null ? strategies : Collections.emptyList();
    }

    /**
     * Evaluates incoming biometric pulse against all clinical safety strategies.
     * Returns the first triggered alert message, if any.
     */
    public Optional<String> evaluateCrisis(CrisisContext context) {
        for (CrisisDetectionStrategy strategy : strategies) {
            try {
                if (strategy.evaluate(context)) {
                    String alert = strategy.getAlertMessage(context);
                    log.warn("Crisis rule '{}' triggered for patient: {}", strategy.getRuleName(), alert);
                    return Optional.of(alert);
                }
            } catch (Exception e) {
                log.error("Error evaluating crisis strategy {}", strategy.getRuleName(), e);
            }
        }
        return Optional.empty();
    }

    public List<CrisisDetectionStrategy> getStrategies() {
        return Collections.unmodifiableList(strategies);
    }
}
