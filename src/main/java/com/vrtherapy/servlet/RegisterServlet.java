package com.vrtherapy.servlet;

import com.vrtherapy.dto.UserRegistrationDto;
import com.vrtherapy.exception.ValidationException;
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

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends BaseServlet {

    private final UserService userService = new UserServiceImpl();
    private final AuditLogService auditLogService = new AuditLogServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        if (currentUser != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        forward(req, resp, "auth/register");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String roleStr = req.getParameter("role");
        String phone = req.getParameter("phone");
        String bio = req.getParameter("clinicalBioOrNotes");

        try {
            UserRegistrationDto dto = new UserRegistrationDto();
            dto.setUsername(username);
            dto.setPassword(password);
            dto.setFullName(fullName);
            dto.setEmail(email);
            dto.setPhone(phone);
            dto.setClinicalBioOrNotes(bio);

            Role role = Role.PATIENT;
            if ("THERAPIST".equalsIgnoreCase(roleStr)) {
                role = Role.THERAPIST;
                dto.setLicenseNumber(req.getParameter("licenseNumber"));
                dto.setSpecialization(req.getParameter("specialization"));
            }
            dto.setRole(role);

            User registered = userService.registerUser(dto);
            auditLogService.recordAction(registered.getUsername(), "USER_REGISTER",
                    "New account created with role " + registered.getRole(), req.getRemoteAddr());

            resp.sendRedirect(req.getContextPath() + "/login?registered=true");
        } catch (ValidationException ex) {
            req.setAttribute("errorMessage", ex.getMessage());
            req.setAttribute("username", username);
            req.setAttribute("fullName", fullName);
            req.setAttribute("email", email);
            req.setAttribute("phone", phone);
            forward(req, resp, "auth/register");
        }
    }
}
