package com.vrtherapy.servlet;

import com.vrtherapy.model.ConditionType;
import com.vrtherapy.model.EnvironmentType;
import com.vrtherapy.model.TherapyPlan;
import com.vrtherapy.model.User;
import com.vrtherapy.service.TherapyPlanService;
import com.vrtherapy.service.UserService;
import com.vrtherapy.service.impl.TherapyPlanServiceImpl;
import com.vrtherapy.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "PlanServlet", urlPatterns = {"/therapist/plan"})
public class PlanServlet extends BaseServlet {

    private final TherapyPlanService planService = new TherapyPlanServiceImpl();
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User therapist = getCurrentUser(req);
        req.setAttribute("plans", planService.getPlansForTherapist(therapist));
        req.setAttribute("patients", userService.getPatientsByTherapist(therapist.getId()));
        req.setAttribute("conditions", ConditionType.values());
        req.setAttribute("environments", EnvironmentType.values());
        forward(req, resp, "therapist/plan");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User therapist = getCurrentUser(req);
        try {
            Long patientId = Long.parseLong(req.getParameter("patientId"));
            String title = req.getParameter("title");
            String clinicalGoal = req.getParameter("goals");
            ConditionType condition = ConditionType.valueOf(req.getParameter("conditionType"));
            int totalWeeks = Integer.parseInt(req.getParameter("totalWeeks"));

            TherapyPlan plan = planService.createPlan(therapist, patientId, title, clinicalGoal, condition, totalWeeks);

            // Add standard starting tiers
            planService.addExposureLevel(plan.getId(), 1, "Tier 1: Grounding & Baseline Coherence",
                    "Acclimate to VR space with guided breathing.", EnvironmentType.CALM_BEACH, 1, 5);
            planService.addExposureLevel(plan.getId(), 2, "Tier 2: Mild Virtual Challenge",
                    "Introduce mild exposure stimuli under regulation cues.", EnvironmentType.FOREST_WALK, 2, 4);

            resp.sendRedirect(req.getContextPath() + "/therapist/hierarchy?planId=" + plan.getId());
        } catch (Exception ex) {
            req.setAttribute("errorMessage", "Failed to create plan: " + ex.getMessage());
            doGet(req, resp);
        }
    }
}
