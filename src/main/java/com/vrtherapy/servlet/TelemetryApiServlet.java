package com.vrtherapy.servlet;

import com.vrtherapy.model.LiveSession;
import com.vrtherapy.model.TelemetrySample;
import com.vrtherapy.model.User;
import com.vrtherapy.service.LiveSessionService;
import com.vrtherapy.service.TelemetryService;
import com.vrtherapy.service.impl.LiveSessionServiceImpl;
import com.vrtherapy.service.impl.TelemetryServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * High-frequency AJAX polling endpoint (called every 2 seconds) for telemetry retrieval
 * and live streaming biometrics.
 */
@WebServlet(name = "TelemetryApiServlet", urlPatterns = {"/api/telemetry"})
public class TelemetryApiServlet extends BaseServlet {

    private final TelemetryService telemetryService = new TelemetryServiceImpl();
    private final LiveSessionService liveSessionService = new LiveSessionServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String liveIdStr = req.getParameter("liveSessionId");
        if (liveIdStr == null || liveIdStr.isEmpty()) {
            sendErrorJson(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing liveSessionId parameter");
            return;
        }

        Long liveSessionId = Long.parseLong(liveIdStr);
        List<TelemetrySample> samples = telemetryService.getSamplesForSession(liveSessionId);
        LiveSession session = liveSessionService.getLiveSession(liveSessionId).orElse(null);

        Map<String, Object> data = new HashMap<>();
        data.put("liveSessionId", liveSessionId);
        data.put("currentHeartRate", session != null ? session.getCurrentHeartRate() : 75);
        data.put("currentStressScore", session != null ? session.getCurrentStressScore() : 4);
        data.put("elapsedSeconds", session != null ? session.getElapsedSeconds() : 0);
        data.put("state", session != null ? session.getState().name() : "UNKNOWN");
        data.put("currentEnvironment", session != null ? session.getCurrentEnvironment().name() : "CALM_BEACH");
        data.put("currentIntensity", session != null ? session.getCurrentIntensity() : 1);
        data.put("breathingGuideActive", session != null && session.isBreathingGuideActive());
        data.put("samples", samples);

        sendJson(resp, data);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = getCurrentUser(req);
        try {
            Long liveSessionId = Long.parseLong(req.getParameter("liveSessionId"));
            int heartRate = Integer.parseInt(req.getParameter("heartRate"));
            int stressScore = Integer.parseInt(req.getParameter("stressScore"));

            TelemetrySample sample = telemetryService.processSample(liveSessionId, user, heartRate, stressScore);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("sampleId", sample.getId());
            sendJson(resp, responseData);
        } catch (Exception ex) {
            sendErrorJson(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }
}
