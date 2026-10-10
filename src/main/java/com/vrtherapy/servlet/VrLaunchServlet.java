package com.vrtherapy.servlet;

import com.vrtherapy.dto.SessionResultSubmissionDto;
import com.vrtherapy.model.LiveSession;
import com.vrtherapy.model.TherapySession;
import com.vrtherapy.model.User;
import com.vrtherapy.service.LiveSessionService;
import com.vrtherapy.service.SessionResultService;
import com.vrtherapy.service.TherapySessionService;
import com.vrtherapy.service.WellbeingService;
import com.vrtherapy.service.impl.LiveSessionServiceImpl;
import com.vrtherapy.service.impl.SessionResultServiceImpl;
import com.vrtherapy.service.impl.TherapySessionServiceImpl;
import com.vrtherapy.service.impl.WellbeingServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "VrLaunchServlet", urlPatterns = {"/vr/launch"})
public class VrLaunchServlet extends BaseServlet {

    private final TherapySessionService sessionService = new TherapySessionServiceImpl();
    private final LiveSessionService liveSessionService = new LiveSessionServiceImpl();
    private final SessionResultService resultService = new SessionResultServiceImpl();
    private final WellbeingService wellbeingService = new WellbeingServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sessionIdStr = req.getParameter("sessionId");
        String liveIdStr = req.getParameter("liveId");

        TherapySession session = null;
        if (sessionIdStr != null && !sessionIdStr.isEmpty()) {
            session = sessionService.getSessionById(Long.parseLong(sessionIdStr));
        } else if (liveIdStr != null && !liveIdStr.isEmpty()) {
            LiveSession live = liveSessionService.getLiveSession(Long.parseLong(liveIdStr)).orElse(null);
            if (live != null) {
                session = sessionService.getSessionById(live.getSessionId());
                req.setAttribute("liveSession", live);
            }
        }

        if (session == null) {
            // Default to first active session template
            var list = sessionService.getAllActiveSessions();
            if (!list.isEmpty()) {
                session = list.get(0);
            }
        }

        req.setAttribute("sessionTemplate", session);
        req.setAttribute("configJson", session != null ? session.getConfigJson() : "{}");
        forward(req, resp, "vr/launch");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User patient = getCurrentUser(req);
        try {
            Long sessionId = Long.parseLong(req.getParameter("sessionId"));
            int preStress = Integer.parseInt(req.getParameter("preStressScore"));
            int postStress = Integer.parseInt(req.getParameter("postStressScore"));
            int durationSeconds = Integer.parseInt(req.getParameter("durationSeconds"));
            int avgHeartRate = Integer.parseInt(req.getParameter("avgHeartRate"));
            int peakHeartRate = Integer.parseInt(req.getParameter("peakHeartRate"));
            boolean emergencyStopped = "true".equalsIgnoreCase(req.getParameter("emergencyStopped"));
            String stopReason = req.getParameter("stopReason");
            String notes = req.getParameter("completionNotes");

            SessionResultSubmissionDto dto = new SessionResultSubmissionDto();
            dto.setSessionId(sessionId);
            dto.setPreStressRating(preStress);
            dto.setPostStressRating(postStress);
            dto.setDurationSeconds(durationSeconds);
            dto.setAverageHeartRate(avgHeartRate);
            dto.setPeakHeartRate(peakHeartRate);
            dto.setEmergencyStopped(emergencyStopped);
            dto.setEmergencyStopReason(stopReason);
            dto.setClinicalNotes(notes);

            resultService.recordResult(dto, patient);

            // Trigger badge evaluation in Java
            wellbeingService.evaluateAndAwardBadges(patient);

            resp.sendRedirect(req.getContextPath() + "/feedback?sessionId=" + sessionId + "&resultRecorded=true");
        } catch (Exception ex) {
            req.setAttribute("errorMessage", "Failed to save session result: " + ex.getMessage());
            doGet(req, resp);
        }
    }
}
