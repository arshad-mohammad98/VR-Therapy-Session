package com.vrtherapy.servlet;

import com.vrtherapy.model.MoodEntry;
import com.vrtherapy.model.User;
import com.vrtherapy.service.WellbeingService;
import com.vrtherapy.service.impl.WellbeingServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "MoodJournalServlet", urlPatterns = {"/patient/mood-journal"})
public class MoodJournalServlet extends BaseServlet {

    private final WellbeingService wellbeingService = new WellbeingServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User patient = getCurrentUser(req);
        List<MoodEntry> entries = wellbeingService.getMoodHistory(patient);
        req.setAttribute("entries", entries);
        forward(req, resp, "patient/mood-journal");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User patient = getCurrentUser(req);
        try {
            int score = Integer.parseInt(req.getParameter("score"));
            int energy = Integer.parseInt(req.getParameter("energy"));
            String emotion = req.getParameter("primaryEmotion");
            String notes = req.getParameter("notes");

            wellbeingService.logMood(patient, score, energy, emotion, notes);
            resp.sendRedirect(req.getContextPath() + "/patient/mood-journal?saved=true");
        } catch (Exception ex) {
            req.setAttribute("errorMessage", ex.getMessage());
            doGet(req, resp);
        }
    }
}
