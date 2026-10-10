package com.vrtherapy.servlet;

import com.vrtherapy.model.Patient;
import com.vrtherapy.model.Role;
import com.vrtherapy.model.User;
import com.vrtherapy.service.DevicePairingService;
import com.vrtherapy.service.LiveSessionService;
import com.vrtherapy.service.SessionAssignmentService;
import com.vrtherapy.service.SessionResultService;
import com.vrtherapy.service.SystemMetricsService;
import com.vrtherapy.service.TherapyPlanService;
import com.vrtherapy.service.TherapySessionService;
import com.vrtherapy.service.UserService;
import com.vrtherapy.service.WellbeingService;
import com.vrtherapy.service.impl.DevicePairingServiceImpl;
import com.vrtherapy.service.impl.LiveSessionServiceImpl;
import com.vrtherapy.service.impl.SessionAssignmentServiceImpl;
import com.vrtherapy.service.impl.SessionResultServiceImpl;
import com.vrtherapy.service.impl.SystemMetricsServiceImpl;
import com.vrtherapy.service.impl.TherapyPlanServiceImpl;
import com.vrtherapy.service.impl.TherapySessionServiceImpl;
import com.vrtherapy.service.impl.UserServiceImpl;
import com.vrtherapy.service.impl.WellbeingServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Main application dashboard dispatcher routing users to their role-specific cockpit
 * (Therapist Command Center, Patient Self-Care Hub, or Administrator Systems Panel).
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard", ""})
public class DashboardServlet extends BaseServlet {

    private final UserService userService = new UserServiceImpl();
    private final TherapySessionService therapySessionService = new TherapySessionServiceImpl();
    private final TherapyPlanService therapyPlanService = new TherapyPlanServiceImpl();
    private final LiveSessionService liveSessionService = new LiveSessionServiceImpl();
    private final SessionAssignmentService sessionAssignmentService = new SessionAssignmentServiceImpl();
    private final SessionResultService sessionResultService = new SessionResultServiceImpl();
    private final DevicePairingService devicePairingService = new DevicePairingServiceImpl();
    private final WellbeingService wellbeingService = new WellbeingServiceImpl();
    private final SystemMetricsService systemMetricsService = new SystemMetricsServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getCurrentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (user.getRole() == Role.THERAPIST) {
            req.setAttribute("patients", userService.getPatientsByTherapist(user.getId()));
            req.setAttribute("sessions", therapySessionService.getSessionsByTherapist(user));
            req.setAttribute("plans", therapyPlanService.getPlansForTherapist(user));
            req.setAttribute("activeLiveSessions", liveSessionService.getActiveSessions());
            req.setAttribute("recentResults", sessionResultService.getResultsForTherapist(user));
            forward(req, resp, "therapist/dashboard");
        } else if (user.getRole() == Role.PATIENT) {
            req.setAttribute("assignments", sessionAssignmentService.getUpcomingAssignmentsForPatient(user));
            req.setAttribute("completedResults", sessionResultService.getResultsForPatient(user));
            req.setAttribute("devices", devicePairingService.getDevicesForPatient(user));
            req.setAttribute("moods", wellbeingService.getMoodHistory(user));
            req.setAttribute("homeworkList", wellbeingService.getHomeworkForPatient(user));
            req.setAttribute("badges", wellbeingService.getBadgesForPatient(user));
            forward(req, resp, "patient/dashboard");
        } else {
            // ADMIN
            req.setAttribute("systemStatus", systemMetricsService.getSystemStatus());
            req.setAttribute("allUsers", userService.getAllUsers());
            req.setAttribute("allDevices", devicePairingService.getAllDevices());
            forward(req, resp, "admin/dashboard");
        }
    }
}
