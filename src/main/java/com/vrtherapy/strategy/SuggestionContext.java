package com.vrtherapy.strategy;

import com.vrtherapy.model.Feedback;
import com.vrtherapy.model.SessionResult;
import com.vrtherapy.model.TherapySession;
import java.util.List;

/**
 * Context container holding clinical and telemetry history used by Suggestion Strategies.
 */
public class SuggestionContext {

    private final TherapySession session;
    private final List<SessionResult> results;
    private final List<Feedback> feedbacks;
    private final double averagePreStress;
    private final double averagePostStress;
    private final double averageRating;
    private final double averageComfortLevel;
    private final double averageHeartRate;
    private final int peakHeartRateObserved;

    public SuggestionContext(TherapySession session, List<SessionResult> results, List<Feedback> feedbacks) {
        this.session = session;
        this.results = results != null ? results : List.of();
        this.feedbacks = feedbacks != null ? feedbacks : List.of();

        this.averagePreStress = this.results.stream()
                .mapToInt(SessionResult::getPreStressRating)
                .average().orElse(0.0);

        this.averagePostStress = this.results.stream()
                .mapToInt(SessionResult::getPostStressRating)
                .average().orElse(0.0);

        this.averageRating = this.feedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average().orElse(0.0);

        this.averageComfortLevel = this.feedbacks.stream()
                .mapToInt(Feedback::getComfortLevel)
                .average().orElse(0.0);

        this.averageHeartRate = this.results.stream()
                .mapToInt(SessionResult::getAverageHeartRate)
                .average().orElse(0.0);

        this.peakHeartRateObserved = this.results.stream()
                .mapToInt(SessionResult::getPeakHeartRate)
                .max().orElse(0);
    }

    public TherapySession getSession() {
        return session;
    }

    public List<SessionResult> getResults() {
        return results;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public double getAveragePreStress() {
        return averagePreStress;
    }

    public double getAveragePostStress() {
        return averagePostStress;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public double getAverageComfortLevel() {
        return averageComfortLevel;
    }

    public double getAverageHeartRate() {
        return averageHeartRate;
    }

    public int getPeakHeartRateObserved() {
        return peakHeartRateObserved;
    }

    public boolean hasData() {
        return !results.isEmpty() || !feedbacks.isEmpty();
    }
}
