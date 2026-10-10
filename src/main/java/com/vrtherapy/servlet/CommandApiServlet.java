package com.vrtherapy.servlet;

import com.vrtherapy.thread.CommandQueue;
import com.vrtherapy.thread.VrCommand;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * High-frequency AJAX polling endpoint (called every 2 seconds by patient WebXR frontend)
 * to receive real-time therapist steering commands (intensity, breathing cues, prompts, emergency stop).
 */
@WebServlet(name = "CommandApiServlet", urlPatterns = {"/api/commands"})
public class CommandApiServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String liveIdStr = req.getParameter("liveSessionId");
        if (liveIdStr == null || liveIdStr.isEmpty()) {
            sendErrorJson(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing liveSessionId parameter");
            return;
        }

        Long liveSessionId = Long.parseLong(liveIdStr);
        List<VrCommand> commands = CommandQueue.getInstance().drainAllCommands(liveSessionId);

        Map<String, Object> result = new HashMap<>();
        result.put("liveSessionId", liveSessionId);
        result.put("commandCount", commands.size());
        result.put("commands", commands);

        sendJson(resp, result);
    }
}
