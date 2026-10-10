package com.vrtherapy.servlet;

import com.vrtherapy.model.User;
import com.vrtherapy.service.AuditLogService;
import com.vrtherapy.service.impl.AuditLogServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends BaseServlet {

    private final AuditLogService auditLogService = new AuditLogServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("currentUser");
            if (user != null) {
                auditLogService.recordAction(user.getUsername(), "USER_LOGOUT",
                        "User logged out from IP " + req.getRemoteAddr(), req.getRemoteAddr());
            }
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login?loggedOut=true");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }
}
