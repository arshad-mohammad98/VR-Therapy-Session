package com.vrtherapy.servlet;

import com.vrtherapy.exception.AuthenticationException;
import com.vrtherapy.model.User;
import com.vrtherapy.service.AuditLogService;
import com.vrtherapy.service.UserService;
import com.vrtherapy.service.impl.AuditLogServiceImpl;
import com.vrtherapy.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller handling user authentication, session binding, and audit trail logging.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends BaseServlet {

    private final UserService userService = new UserServiceImpl();
    private final AuditLogService auditLogService = new AuditLogServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        if (currentUser != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        forward(req, resp, "auth/login");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try {
            User user = userService.authenticate(username, password);
            HttpSession session = req.getSession(true);
            session.setAttribute("currentUser", user);

            auditLogService.recordAction(user.getUsername(), "USER_LOGIN",
                    "User logged in from IP " + req.getRemoteAddr(), req.getRemoteAddr());

            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } catch (AuthenticationException ex) {
            req.setAttribute("errorMessage", ex.getMessage());
            req.setAttribute("username", username);
            forward(req, resp, "auth/login");
        }
    }
}
