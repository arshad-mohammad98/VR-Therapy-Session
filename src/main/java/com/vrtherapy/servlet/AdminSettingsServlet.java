package com.vrtherapy.servlet;

import com.vrtherapy.model.User;
import com.vrtherapy.service.AuditLogService;
import com.vrtherapy.service.SystemSettingService;
import com.vrtherapy.service.impl.AuditLogServiceImpl;
import com.vrtherapy.service.impl.SystemSettingServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AdminSettingsServlet", urlPatterns = {"/admin/settings"})
public class AdminSettingsServlet extends BaseServlet {

    private final SystemSettingService settingService = new SystemSettingServiceImpl();
    private final AuditLogService auditLogService = new AuditLogServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("settings", settingService.getAllSettings());
        forward(req, resp, "admin/settings");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = getCurrentUser(req);
        String key = req.getParameter("settingKey");
        String value = req.getParameter("settingValue");
        String desc = req.getParameter("description");

        settingService.setSetting(key, value, desc, admin.getUsername());
        auditLogService.recordAction(admin.getUsername(), "SETTING_CHANGED",
                "Updated setting '" + key + "' = '" + value + "'", req.getRemoteAddr());

        resp.sendRedirect(req.getContextPath() + "/admin/settings?saved=true");
    }
}
