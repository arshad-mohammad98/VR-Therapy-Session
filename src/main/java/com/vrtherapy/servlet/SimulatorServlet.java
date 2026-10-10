package com.vrtherapy.servlet;

import com.vrtherapy.service.DevicePairingService;
import com.vrtherapy.service.LiveSessionService;
import com.vrtherapy.service.impl.DevicePairingServiceImpl;
import com.vrtherapy.service.impl.LiveSessionServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller serving the Simulated Headset WebXR environment.
 * Allows full project demonstration and testing without requiring physical VR headset hardware.
 */
@WebServlet(name = "SimulatorServlet", urlPatterns = {"/simulator"})
public class SimulatorServlet extends BaseServlet {

    private final DevicePairingService devicePairingService = new DevicePairingServiceImpl();
    private final LiveSessionService liveSessionService = new LiveSessionServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("activeLiveSessions", liveSessionService.getActiveSessions());
        forward(req, resp, "simulator/index");
    }
}
