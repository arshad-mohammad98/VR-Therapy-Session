package com.vrtherapy.servlet;

import com.vrtherapy.model.LiveSession;
import com.vrtherapy.model.User;
import com.vrtherapy.service.LiveSessionService;
import com.vrtherapy.service.impl.LiveSessionServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller executing immediate clinical emergency halt.
 * Transitions session to EMERGENCY_STOPPED, dispatches disconnect cues, alerts therapist,
 * and renders grounding instructions with crisis resources.
 */
@WebServlet(name = "EmergencyStopServlet", urlPatterns = {"/api/emergency-stop", "/patient/emergency-grounding"})
public class EmergencyStopServlet extends BaseServlet {

    private final LiveSessionService liveSessionService = new LiveSessionServiceImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getCurrentUser(req);
        String liveIdStr = req.getParameter("liveSessionId");
        String reason = req.getParameter("reason");

        if (liveIdStr != null && !liveIdStr.isEmpty()) {
            Long liveSessionId = Long.parseLong(liveIdStr);
            String actorName = user != null ? user.getUsername() : "Patient in VR";
            liveSessionService.triggerEmergencyStop(liveSessionId, reason != null ? reason : "Patient pressed in-VR emergency stop", actorName);
        }

        String isAjax = req.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equalsIgnoreCase(isAjax) || req.getContentType() != null && req.getContentType().contains("json")) {
            Map<String, Object> data = new HashMap<>();
            data.put("emergencyStopped", true);
            data.put("groundingTitle", "5-4-3-2-1 Somatosensory Grounding Protocol");
            data.put("groundingSteps", new String[]{
                    "1. Acknowledge 5 things you can see around your physical room.",
                    "2. Acknowledge 4 things you can physically touch (feel your feet flat on the floor).",
                    "3. Acknowledge 3 sounds you can hear in the distance.",
                    "4. Acknowledge 2 scents you can smell.",
                    "5. Take 1 slow, deep diaphragmatic breath (inhale for 4, exhale for 6)."
            });
            data.put("crisisHelpline", "National Crisis Lifeline: Dial 988 or text HOME to 741741 (Available 24/7).");
            sendJson(resp, data);
            return;
        }

        // HTML redirect to emergency landing
        req.setAttribute("reason", reason);
        forward(req, resp, "patient/emergency-grounding");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        forward(req, resp, "patient/emergency-grounding");
    }
}
