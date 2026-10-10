package com.vrtherapy.service.impl;

import com.vrtherapy.dao.FeedbackDao;
import com.vrtherapy.dao.SessionDao;
import com.vrtherapy.dao.SessionResultDao;
import com.vrtherapy.dao.impl.FeedbackDaoImpl;
import com.vrtherapy.dao.impl.SessionDaoImpl;
import com.vrtherapy.dao.impl.SessionResultDaoImpl;
import com.vrtherapy.model.Feedback;
import com.vrtherapy.model.SessionResult;
import com.vrtherapy.model.TherapySession;
import com.vrtherapy.service.SuggestionService;
import com.vrtherapy.strategy.SessionAdjustmentSuggestion;
import com.vrtherapy.strategy.SuggestionContext;
import com.vrtherapy.strategy.SuggestionEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation bridging session telemetry and patient ratings with the
 * Strategy Pattern SuggestionEngine.
 */
public class SuggestionServiceImpl implements SuggestionService {

    private static final Logger logger = LoggerFactory.getLogger(SuggestionServiceImpl.class);

    private final SessionDao sessionDao;
    private final SessionResultDao resultDao;
    private final FeedbackDao feedbackDao;
    private final SuggestionEngine suggestionEngine;

    public SuggestionServiceImpl() {
        this(new SessionDaoImpl(), new SessionResultDaoImpl(), new FeedbackDaoImpl(), new SuggestionEngine());
    }

    public SuggestionServiceImpl(SessionDao sessionDao, SessionResultDao resultDao, FeedbackDao feedbackDao, SuggestionEngine suggestionEngine) {
        this.sessionDao = sessionDao;
        this.resultDao = resultDao;
        this.feedbackDao = feedbackDao;
        this.suggestionEngine = suggestionEngine;
    }

    @Override
    public List<SessionAdjustmentSuggestion> generateSuggestionsForSession(Long sessionId) {
        Optional<TherapySession> optSession = sessionDao.findById(sessionId);
        if (optSession.isEmpty()) {
            return List.of();
        }
        return evaluateSession(optSession.get());
    }

    @Override
    public Optional<SessionAdjustmentSuggestion> getPrimarySuggestionForSession(Long sessionId) {
        List<SessionAdjustmentSuggestion> list = generateSuggestionsForSession(sessionId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<SessionAdjustmentSuggestion> evaluateSession(TherapySession session) {
        List<SessionResult> results = resultDao.findBySessionId(session.getId());
        List<Feedback> feedbacks = feedbackDao.findBySessionId(session.getId());

        int sampleCount = results.size();
        double avgPreStress = results.stream().mapToInt(SessionResult::getPreStressScore).average().orElse(5.0);
        double avgPostStress = results.stream().mapToInt(SessionResult::getPostStressScore).average().orElse(4.0);
        double avgStressDelta = avgPreStress - avgPostStress;
        double avgRating = feedbacks.stream().mapToInt(Feedback::getRating).average().orElse(4.0);
        double avgComfort = feedbacks.stream().mapToInt(Feedback::getComfortScore).average().orElse(4.0);
        double avgNausea = feedbacks.stream().mapToInt(Feedback::getNauseaScore).average().orElse(1.0);
        double avgHeartRate = results.stream().mapToInt(SessionResult::getAverageHeartRate).average().orElse(75.0);
        int peakHeartRate = results.stream().mapToInt(SessionResult::getPeakHeartRate).max().orElse(85);

        SuggestionContext context = new SuggestionContext(
                session,
                sampleCount,
                avgPreStress,
                avgPostStress,
                avgStressDelta,
                avgRating,
                avgComfort,
                avgNausea,
                avgHeartRate,
                peakHeartRate
        );

        List<SessionAdjustmentSuggestion> suggestions = suggestionEngine.findAllSuggestions(context);
        logger.info("Generated {} clinical adjustment suggestions for session {}", suggestions.size(), session.getId());
        return suggestions;
    }
}
