package com.vrtherapy.servlet;

import com.vrtherapy.model.EnvironmentType;
import com.vrtherapy.model.ExposureLevel;
import com.vrtherapy.model.TherapyPlan;
import com.vrtherapy.service.TherapyPlanService;
import com.vrtherapy.service.impl.TherapyPlanServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "HierarchyServlet", urlPatterns = {"/therapist/hierarchy"})
public class HierarchyServlet extends BaseServlet {

    private final TherapyPlanService planService = new TherapyPlanServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String planIdStr = req.getParameter("planId");
        if (planIdStr == null || planIdStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/therapist/plan");
            return;
        }

        Long planId = Long.parseLong(planIdStr);
        Optional<TherapyPlan> optPlan = planService.getPlanById(planId);
        if (optPlan.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/therapist/plan");
            return;
        }

        req.setAttribute("plan", optPlan.get());
        req.setAttribute("levels", planService.getLevelsForPlan(planId));
        req.setAttribute("environments", EnvironmentType.values());
        forward(req, resp, "therapist/hierarchy");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        Long planId = Long.parseLong(req.getParameter("planId"));

        if ("addLevel".equals(action)) {
            int levelOrder = Integer.parseInt(req.getParameter("levelOrder"));
            String title = req.getParameter("title");
            String description = req.getParameter("description");
            EnvironmentType env = EnvironmentType.valueOf(req.getParameter("environment"));
            int intensity = Integer.parseInt(req.getParameter("intensity"));
            int threshold = Integer.parseInt(req.getParameter("threshold"));

            planService.addExposureLevel(planId, levelOrder, title, description, env, intensity, threshold);
        } else if ("evaluateUnlock".equals(action)) {
            Long currentLevelId = Long.parseLong(req.getParameter("currentLevelId"));
            int postStress = Integer.parseInt(req.getParameter("postStress"));
            boolean unlocked = planService.evaluateAndUnlockNextLevel(planId, currentLevelId, postStress);
            if (unlocked) {
                req.getSession().setAttribute("flashMessage", "Criteria met! Next exposure level successfully unlocked.");
            } else {
                req.getSession().setAttribute("flashError", "Distress score (" + postStress + ") exceeded threshold. Tier remains locked.");
            }
        }

        resp.sendRedirect(req.getContextPath() + "/therapist/hierarchy?planId=" + planId);
    }
}
