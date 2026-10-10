package com.vrtherapy.filter;

import com.vrtherapy.model.Role;
import com.vrtherapy.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Role-Based Access Control (RBAC) filter protecting URLs according to User role.
 */
@WebFilter(filterName = "RoleFilter", urlPatterns = {"/therapist/*", "/patient/*", "/admin/*"})
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User user = session != null ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getRequestURI().substring(req.getContextPath().length());

        if (path.startsWith("/admin") && user.getRole() != Role.ADMIN) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Requires Administrator role.");
            return;
        }

        if (path.startsWith("/therapist") && (user.getRole() != Role.THERAPIST && user.getRole() != Role.ADMIN)) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Requires Therapist privileges.");
            return;
        }

        if (path.startsWith("/patient") && (user.getRole() != Role.PATIENT && user.getRole() != Role.ADMIN)) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Requires Patient account.");
            return;
        }

        chain.doFilter(request, response);
    }
}
