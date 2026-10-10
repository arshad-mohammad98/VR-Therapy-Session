package com.vrtherapy.servlet;

import com.vrtherapy.dto.FeedbackSubmissionDto;
import com.vrtherapy.model.Feedback;
import com.vrtherapy.model.Role;
import com.vrtherapy.model.TherapySession;
import com.vrtherapy.model.User;
import com.vrtherapy.service.FeedbackService;
import com.vrtherapy.service.SuggestionService;
import com.vrtherapy.service.TherapySessionService;
import com.vrtherapy.service.impl.FeedbackServiceImpl;
import com.vrtherapy.service.impl.SuggestionServiceImpl;
import com.vrtherapy.service.impl.TherapySessionServiceImpl;
import com.vrtherapy.strategy.SessionAdjustmentSuggestion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "FeedbackServlet", urlPatterns = {"/feedback", "/therapist/feedback"})
public class FeedbackServlet extends BaseServlet {

    private final FeedbackService feedbackService = new FeedbackServiceImpl();
    private final SuggestionService suggestionService = new SuggestionServiceImpl();
    private final TherapySessionService sessionService = new TherapySessionServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getCurrentUser(req);
        if (user.getRole() == Role.THERAPIST || user.getRole() == Role.ADMIN) {
            List<TherapySession> sessions = sessionService.getSessionsByTherapist(user);
            Map<Long, List<SessionAdjustmentSuggestion>> suggestionsMap = new HashMap<>();
            for (TherapySession s : sessions) {
                suggestionsMap.put(s.getId(), suggestionService.generateSuggestionsForSession(s.getId()));
            }

            req.setAttribute("sessions", sessions);
            req.setAttribute("feedbacks", feedbackService.getFeedbacksForTherapist(user));
            req.setAttribute("suggestionsMap", suggestionsMap);
            forward(req, resp, "therapist/feedback");
        } else {
            // Patient view
            String sessionId = req.getParameter("sessionId");
            req.setAttribute("sessionId", sessionId);
            req.setAttribute("feedbacks", feedbackService.getFeedbacksByPatient(user));
            forward(req, resp, "patient/feedback");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getCurrentUser(req);
        String action = req.getParameter("action");

        if ("applyAdjustment".equals(action)) {
            Long sessionId = Long.parseLong(req.getParameter("sessionId"));
            String intensityStr = req.getParameter("intensity");
            String durationStr = req.getParameter("duration");

            SessionAdjustmentSuggestion suggestion = new SessionAdjustmentSuggestion(
                    "Manual clinical tuning applied from suggestions engine",
                    intensityStr != null && !intensityStr.isEmpty() ? Integer.parseInt(intensityStr) : null,
                    durationStr != null && !durationStr.isEmpty() ? Integer.parseInt(durationStr) : null
            );
            sessionService.applySuggestionAdjustment(sessionId, suggestion, user);
            resp.sendRedirect(req.getContextPath() + "/therapist/feedback?applied=true");
            return;
        }

        // Patient submitting feedback
        FeedbackSubmissionDto dto = new FeedbackSubmissionDto();
        dto.setSessionId(Long.parseLong(req.getParameter("sessionId")));
        dto.setRating(Integer.parseInt(req.getParameter("rating")));
        dto.setComfortScore(Integer.parseInt(req.getParameter("comfortScore")));
        dto.setNauseaScore(Integer.parseInt(req.getParameter("nauseaScore")));
        dto.setComments(req.getParameter("comments"));

        feedbackService.submitFeedback(dto, user);
        resp.sendRedirect(req.getContextPath() + "/patient/feedback?submitted=true");
    }
}
