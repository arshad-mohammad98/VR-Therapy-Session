package com.vrtherapy.servlet;

import com.vrtherapy.model.EnvironmentType;
import com.vrtherapy.model.LiveSession;
import com.vrtherapy.model.LiveSessionState;
import com.vrtherapy.model.User;
import com.vrtherapy.service.LiveSessionService;
import com.vrtherapy.service.TherapySessionService;
import com.vrtherapy.service.UserService;
import com.vrtherapy.service.impl.LiveSessionServiceImpl;
import com.vrtherapy.service.impl.TherapySessionServiceImpl;
import com.vrtherapy.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "LiveControlServlet", urlPatterns = {"/therapist/live-control"})
public class LiveControlServlet extends BaseServlet {

    private final LiveSessionService liveSessionService = new LiveSessionServiceImpl();
    private final UserService userService = new UserServiceImpl();
    private final TherapySessionService therapySessionService = new TherapySessionServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User therapist = getCurrentUser(req);
        String sessionIdStr = req.getParameter("sessionId");

        LiveSession activeSession = null;
        if (sessionIdStr != null && !sessionIdStr.isEmpty()) {
            activeSession = liveSessionService.getLiveSession(Long.parseLong(sessionIdStr)).orElse(null);
        } else {
            // Find any active session for this therapist
            var sessions = liveSessionService.getActiveSessions();
            if (!sessions.isEmpty()) {
                activeSession = sessions.get(0);
            }
        }

        req.setAttribute("activeSession", activeSession);
        req.setAttribute("patients", userService.getPatientsByTherapist(therapist.getId()));
        req.setAttribute("sessionTemplates", therapySessionService.getSessionsByTherapist(therapist));
        req.setAttribute("environments", EnvironmentType.values());
        forward(req, resp, "therapist/live-control");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User therapist = getCurrentUser(req);
        String action = req.getParameter("action");
        String sessionIdStr = req.getParameter("sessionId");
        Long sessionId = (sessionIdStr != null && !sessionIdStr.isEmpty()) ? Long.parseLong(sessionIdStr) : null;

        if ("startNew".equals(action)) {
            Long patientId = Long.parseLong(req.getParameter("patientId"));
            Long templateId = Long.parseLong(req.getParameter("templateId"));
            EnvironmentType env = EnvironmentType.valueOf(req.getParameter("environment"));
            int intensity = Integer.parseInt(req.getParameter("intensity"));

            LiveSession created = liveSessionService.createLiveSession(therapist, patientId, templateId, env, intensity);
            liveSessionService.transitionState(created.getId(), LiveSessionState.ACTIVE, "Therapist started live session");
            resp.sendRedirect(req.getContextPath() + "/therapist/live-control?sessionId=" + created.getId());
            return;
        }

        if (sessionId != null) {
            switch (action) {
                case "changeIntensity" -> {
                    int intensity = Integer.parseInt(req.getParameter("intensity"));
                    liveSessionService.updateIntensity(sessionId, intensity);
                }
                case "changeEnvironment" -> {
                    EnvironmentType env = EnvironmentType.valueOf(req.getParameter("environment"));
                    liveSessionService.updateEnvironment(sessionId, env);
                }
                case "toggleBreathing" -> {
                    boolean active = Boolean.parseBoolean(req.getParameter("active"));
                    liveSessionService.toggleGuidedBreathing(sessionId, active);
                }
                case "sendPrompt" -> {
                    String promptText = req.getParameter("promptText");
                    liveSessionService.sendVrPrompt(sessionId, promptText);
                }
                case "pause" -> liveSessionService.transitionState(sessionId, LiveSessionState.PAUSED, "Therapist paused session");
                case "resume" -> liveSessionService.transitionState(sessionId, LiveSessionState.ACTIVE, "Therapist resumed session");
                case "complete" -> liveSessionService.transitionState(sessionId, LiveSessionState.COMPLETED, "Concluded normally");
                case "emergencyStop" -> {
                    String reason = req.getParameter("reason");
                    liveSessionService.triggerEmergencyStop(sessionId, reason != null ? reason : "Therapist triggered emergency stop", therapist.getUsername());
                }
            }
        }

        resp.sendRedirect(req.getContextPath() + "/therapist/live-control?sessionId=" + sessionId);
    }
}
