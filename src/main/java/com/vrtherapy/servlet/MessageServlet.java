package com.vrtherapy.servlet;

import com.vrtherapy.model.Message;
import com.vrtherapy.model.Patient;
import com.vrtherapy.model.Role;
import com.vrtherapy.model.User;
import com.vrtherapy.service.UserService;
import com.vrtherapy.service.WellbeingService;
import com.vrtherapy.service.impl.UserServiceImpl;
import com.vrtherapy.service.impl.WellbeingServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "MessageServlet", urlPatterns = {"/messages"})
public class MessageServlet extends BaseServlet {

    private final WellbeingService wellbeingService = new WellbeingServiceImpl();
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        String otherUserIdStr = req.getParameter("userId");

        User otherUser = null;
        if (otherUserIdStr != null && !otherUserIdStr.isEmpty()) {
            otherUser = userService.getUserById(Long.parseLong(otherUserIdStr));
        } else {
            // Pick default chat partner
            if (currentUser.getRole() == Role.PATIENT && currentUser instanceof Patient p && p.getAssignedTherapistId() != null) {
                otherUser = userService.getUserById(p.getAssignedTherapistId());
            } else if (currentUser.getRole() == Role.THERAPIST) {
                var patients = userService.getPatientsByTherapist(currentUser.getId());
                if (!patients.isEmpty()) {
                    otherUser = patients.get(0);
                }
            }
        }

        List<Message> conversation = List.of();
        if (otherUser != null) {
            conversation = wellbeingService.getConversation(currentUser, otherUser);
            req.setAttribute("chatPartner", otherUser);
        }

        req.setAttribute("conversation", conversation);
        if (currentUser.getRole() == Role.THERAPIST) {
            req.setAttribute("contacts", userService.getPatientsByTherapist(currentUser.getId()));
        } else {
            req.setAttribute("contacts", userService.getActiveTherapists());
        }

        forward(req, resp, "shared/messages");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        Long recipientId = Long.parseLong(req.getParameter("recipientId"));
        String content = req.getParameter("content");

        wellbeingService.sendMessage(currentUser, recipientId, content);
        resp.sendRedirect(req.getContextPath() + "/messages?userId=" + recipientId);
    }
}
