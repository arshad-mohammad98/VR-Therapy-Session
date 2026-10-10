package com.vrtherapy.servlet;

import com.vrtherapy.model.ThoughtRecord;
import com.vrtherapy.model.User;
import com.vrtherapy.service.WellbeingService;
import com.vrtherapy.service.impl.WellbeingServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ThoughtRecordServlet", urlPatterns = {"/patient/thought-record"})
public class ThoughtRecordServlet extends BaseServlet {

    private final WellbeingService wellbeingService = new WellbeingServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User patient = getCurrentUser(req);
        List<ThoughtRecord> records = wellbeingService.getThoughtRecords(patient);
        req.setAttribute("records", records);
        forward(req, resp, "patient/thought-record");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User patient = getCurrentUser(req);
        try {
            String situation = req.getParameter("situation");
            String thought = req.getParameter("automaticThought");
            String emotion = req.getParameter("emotion");
            String distortion = req.getParameter("cognitiveDistortion");
            String alternative = req.getParameter("rationalResponse");
            int outcome = Integer.parseInt(req.getParameter("outcomeBelief"));

            wellbeingService.recordThought(patient, situation, thought, emotion, distortion, alternative, outcome);
            resp.sendRedirect(req.getContextPath() + "/patient/thought-record?saved=true");
        } catch (Exception ex) {
            req.setAttribute("errorMessage", ex.getMessage());
            doGet(req, resp);
        }
    }
}
