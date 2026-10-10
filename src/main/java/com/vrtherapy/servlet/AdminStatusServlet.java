package com.vrtherapy.servlet;

import com.vrtherapy.dto.SystemStatusDto;
import com.vrtherapy.service.AuditLogService;
import com.vrtherapy.service.SystemMetricsService;
import com.vrtherapy.service.impl.AuditLogServiceImpl;
import com.vrtherapy.service.impl.SystemMetricsServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AdminStatusServlet", urlPatterns = {"/admin/status"})
public class AdminStatusServlet extends BaseServlet {

    private final SystemMetricsService metricsService = new SystemMetricsServiceImpl();
    private final AuditLogService auditLogService = new AuditLogServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SystemStatusDto status = metricsService.getSystemStatus();
        req.setAttribute("systemStatus", status);
        req.setAttribute("recentAuditLogs", auditLogService.getRecentLogs());
        forward(req, resp, "admin/status");
    }
}
