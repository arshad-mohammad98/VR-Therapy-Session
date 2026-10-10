package com.vrtherapy.servlet;

import com.vrtherapy.dto.PatientProgressDto;
import com.vrtherapy.model.User;
import com.vrtherapy.service.ProgressAnalyticsService;
import com.vrtherapy.service.UserService;
import com.vrtherapy.service.impl.ProgressAnalyticsServiceImpl;
import com.vrtherapy.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ProgressServlet", urlPatterns = {"/therapist/progress"})
public class ProgressServlet extends BaseServlet {

    private final ProgressAnalyticsService analyticsService = new ProgressAnalyticsServiceImpl();
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User therapist = getCurrentUser(req);
        String patientIdStr = req.getParameter("patientId");

        var patients = userService.getPatientsByTherapist(therapist.getId());
        req.setAttribute("patients", patients);

        User selectedPatient = null;
        if (patientIdStr != null && !patientIdStr.isEmpty()) {
            selectedPatient = userService.getUserById(Long.parseLong(patientIdStr));
        } else if (!patients.isEmpty()) {
            selectedPatient = patients.get(0);
        }

        if (selectedPatient != null) {
            PatientProgressDto progress = analyticsService.calculatePatientProgress(selectedPatient);
            req.setAttribute("selectedPatient", selectedPatient);
            req.setAttribute("progress", progress);
        }

        forward(req, resp, "therapist/progress");
    }
}
