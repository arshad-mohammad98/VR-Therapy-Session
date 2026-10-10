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

        SuggestionContext context = new SuggestionContext(session, results, feedbacks);

        List<SessionAdjustmentSuggestion> suggestions = suggestionEngine.findAllSuggestions(context);
        logger.info("Generated {} clinical adjustment suggestions for session {}", suggestions.size(), session.getId());
        return suggestions;
    }
}
