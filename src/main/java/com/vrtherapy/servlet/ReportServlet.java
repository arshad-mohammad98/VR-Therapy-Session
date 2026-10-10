package com.vrtherapy.servlet;

import com.vrtherapy.model.User;
import com.vrtherapy.service.ProgressAnalyticsService;
import com.vrtherapy.service.UserService;
import com.vrtherapy.service.impl.ProgressAnalyticsServiceImpl;
import com.vrtherapy.service.impl.UserServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;

@WebServlet(name = "ReportServlet", urlPatterns = {"/therapist/report"})
public class ReportServlet extends BaseServlet {

    private final ProgressAnalyticsService analyticsService = new ProgressAnalyticsServiceImpl();
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String patientIdStr = req.getParameter("patientId");
        String format = req.getParameter("format");

        if (patientIdStr == null || patientIdStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/therapist/progress");
            return;
        }

        User patient = userService.getUserById(Long.parseLong(patientIdStr));
        String filenamePrefix = "clinical_report_" + patient.getUsername() + "_" + System.currentTimeMillis();

        if ("csv".equalsIgnoreCase(format)) {
            byte[] csvBytes = analyticsService.generatePatientProgressCsv(patient);
            resp.setContentType("text/csv;charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + filenamePrefix + ".csv\"");
            resp.setContentLength(csvBytes.length);
            try (OutputStream out = resp.getOutputStream()) {
                out.write(csvBytes);
                out.flush();
            }
        } else {
            byte[] txtBytes = analyticsService.generatePatientProgressTextReport(patient);
            resp.setContentType("text/plain;charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + filenamePrefix + ".txt\"");
            resp.setContentLength(txtBytes.length);
            try (OutputStream out = resp.getOutputStream()) {
                out.write(txtBytes);
                out.flush();
            }
        }
    }
}
