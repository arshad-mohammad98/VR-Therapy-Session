package com.vrtherapy.servlet;

import com.vrtherapy.dto.TherapySessionDto;
import com.vrtherapy.exception.ValidationException;
import com.vrtherapy.model.ConditionType;
import com.vrtherapy.model.EnvironmentType;
import com.vrtherapy.model.TherapySession;
import com.vrtherapy.model.User;
import com.vrtherapy.service.AuditLogService;
import com.vrtherapy.service.TherapySessionService;
import com.vrtherapy.service.impl.AuditLogServiceImpl;
import com.vrtherapy.service.impl.TherapySessionServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller allowing clinical therapists to author and tune virtual exposure protocols.
 */
@WebServlet(name = "SessionDesignServlet", urlPatterns = {"/therapist/session-design"})
public class SessionDesignServlet extends BaseServlet {

    private final TherapySessionService sessionService = new TherapySessionServiceImpl();
    private final AuditLogService auditLogService = new AuditLogServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User therapist = getCurrentUser(req);
        req.setAttribute("sessions", sessionService.getSessionsByTherapist(therapist));
        req.setAttribute("conditions", ConditionType.values());
        req.setAttribute("environments", EnvironmentType.values());
        forward(req, resp, "therapist/session-design");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User therapist = getCurrentUser(req);
        try {
            TherapySessionDto dto = new TherapySessionDto();
            dto.setTitle(req.getParameter("title"));
            dto.setConditionType(req.getParameter("conditionType"));
            dto.setVrEnvironment(req.getParameter("vrEnvironment"));
            dto.setDefaultDurationMinutes(Integer.parseInt(req.getParameter("durationMinutes")));
            dto.setIntensityLevel(Integer.parseInt(req.getParameter("intensityLevel")));
            dto.setAudioEnabled("on".equalsIgnoreCase(req.getParameter("audioEnabled")) || "true".equalsIgnoreCase(req.getParameter("audioEnabled")));
            dto.setGuidedBreathingEnabled("on".equalsIgnoreCase(req.getParameter("guidedBreathingEnabled")) || "true".equalsIgnoreCase(req.getParameter("guidedBreathingEnabled")));
            dto.setTherapistNotes(req.getParameter("therapistNotes"));

            TherapySession created = sessionService.createSession(dto, therapist);
            auditLogService.recordAction(therapist.getUsername(), "SESSION_CREATED",
                    "Created session template: " + created.getTitle(), req.getRemoteAddr());

            resp.sendRedirect(req.getContextPath() + "/therapist/session-design?success=true");
        } catch (ValidationException | NumberFormatException ex) {
            req.setAttribute("errorMessage", ex.getMessage());
            req.setAttribute("sessions", sessionService.getSessionsByTherapist(therapist));
            req.setAttribute("conditions", ConditionType.values());
            req.setAttribute("environments", EnvironmentType.values());
            forward(req, resp, "therapist/session-design");
        }
    }
}
