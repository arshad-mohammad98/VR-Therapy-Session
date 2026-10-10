package com.vrtherapy.servlet;

import com.vrtherapy.model.DeviceType;
import com.vrtherapy.model.User;
import com.vrtherapy.model.VrDevice;
import com.vrtherapy.service.DevicePairingService;
import com.vrtherapy.service.impl.DevicePairingServiceImpl;
import com.vrtherapy.thread.VrConnectionRegistry;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "DevicePairingServlet", urlPatterns = {"/patient/device-pairing", "/api/device/pair", "/api/device/ping"})
public class DevicePairingServlet extends BaseServlet {

    private final DevicePairingService pairingService = new DevicePairingServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        // API Ping endpoint for latency measurement
        if ("/api/device/ping".equals(path)) {
            String code = req.getParameter("code");
            long clientTime = 0;
            try {
                clientTime = Long.parseLong(req.getParameter("t"));
            } catch (Exception ignored) {
            }
            long latency = clientTime > 0 ? System.currentTimeMillis() - clientTime : 14;

            if (code != null && !code.isEmpty()) {
                VrConnectionRegistry.getInstance().recordHeartbeat(code, latency);
            }

            Map<String, Object> pingResp = new HashMap<>();
            pingResp.put("pong", true);
            pingResp.put("latencyMs", latency);
            pingResp.put("serverTime", System.currentTimeMillis());
            sendJson(resp, pingResp);
            return;
        }

        User patient = getCurrentUser(req);
        req.setAttribute("devices", pairingService.getDevicesForPatient(patient));
        req.setAttribute("deviceTypes", DeviceType.values());
        forward(req, resp, "patient/device-pairing");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        // API pairing code endpoint called by VR headset or simulator
        if ("/api/device/pair".equals(path)) {
            String pairingCode = req.getParameter("pairingCode");
            String clientName = req.getParameter("clientName");
            try {
                VrDevice device = pairingService.pairDeviceWithCode(pairingCode, clientName, req.getRemoteAddr());
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("deviceId", device.getId());
                result.put("deviceName", device.getDeviceName());
                result.put("status", device.getStatus().name());
                sendJson(resp, result);
            } catch (Exception ex) {
                sendErrorJson(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            }
            return;
        }

        // Web form registration
        User patient = getCurrentUser(req);
        String action = req.getParameter("action");

        if ("register".equals(action)) {
            String deviceName = req.getParameter("deviceName");
            DeviceType type = DeviceType.valueOf(req.getParameter("deviceType"));
            pairingService.registerDevice(patient, deviceName, type, true, true);
        } else if ("generateCode".equals(action)) {
            Long deviceId = Long.parseLong(req.getParameter("deviceId"));
            pairingService.generateNewPairingCode(deviceId, patient);
        } else if ("disconnect".equals(action)) {
            Long deviceId = Long.parseLong(req.getParameter("deviceId"));
            pairingService.disconnectDevice(deviceId);
        }

        resp.sendRedirect(req.getContextPath() + "/patient/device-pairing");
    }
}
