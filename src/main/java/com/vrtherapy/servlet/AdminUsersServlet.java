package com.vrtherapy.servlet;

import com.vrtherapy.model.Role;
import com.vrtherapy.model.User;
import com.vrtherapy.service.AuditLogService;
import com.vrtherapy.service.UserService;
import com.vrtherapy.service.impl.AuditLogServiceImpl;
import com.vrtherapy.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AdminUsersServlet", urlPatterns = {"/admin/users"})
public class AdminUsersServlet extends BaseServlet {

    private final UserService userService = new UserServiceImpl();
    private final AuditLogService auditLogService = new AuditLogServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("users", userService.getAllUsers());
        req.setAttribute("roles", Role.values());
        forward(req, resp, "admin/users");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = getCurrentUser(req);
        String action = req.getParameter("action");
        Long userId = Long.parseLong(req.getParameter("userId"));

        if ("toggleActive".equals(action)) {
            User updated = userService.toggleUserActive(userId);
            auditLogService.recordAction(admin.getUsername(), "USER_STATUS_CHANGE",
                    "User '" + updated.getUsername() + "' active state changed to " + updated.isActive(), req.getRemoteAddr());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/users?updated=true");
    }
}
