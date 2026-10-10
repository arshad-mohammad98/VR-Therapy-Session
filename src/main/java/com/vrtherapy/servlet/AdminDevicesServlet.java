package com.vrtherapy.servlet;

import com.vrtherapy.model.User;
import com.vrtherapy.service.AuditLogService;
import com.vrtherapy.service.DevicePairingService;
import com.vrtherapy.service.impl.AuditLogServiceImpl;
import com.vrtherapy.service.impl.DevicePairingServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AdminDevicesServlet", urlPatterns = {"/admin/devices"})
public class AdminDevicesServlet extends BaseServlet {

    private final DevicePairingService deviceService = new DevicePairingServiceImpl();
    private final AuditLogService auditLogService = new AuditLogServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("devices", deviceService.getAllDevices());
        forward(req, resp, "admin/devices");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = getCurrentUser(req);
        String action = req.getParameter("action");
        Long deviceId = Long.parseLong(req.getParameter("deviceId"));

        if ("disconnect".equals(action)) {
            deviceService.disconnectDevice(deviceId);
            auditLogService.recordAction(admin.getUsername(), "DEVICE_FORCE_DISCONNECT",
                    "Administrator forced disconnect for Device ID " + deviceId, req.getRemoteAddr());
        } else if ("block".equals(action)) {
            deviceService.blockDevice(deviceId);
            auditLogService.recordAction(admin.getUsername(), "DEVICE_BLOCKED",
                    "Administrator blocked Device ID " + deviceId, req.getRemoteAddr());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/devices?updated=true");
    }
}
