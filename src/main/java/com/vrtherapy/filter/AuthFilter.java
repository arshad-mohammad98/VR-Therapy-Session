package com.vrtherapy.filter;

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
import java.util.UUID;

/**
 * Authentication filter ensuring private endpoints are accessible only to logged-in sessions.
 * Also injects CSRF token into active HTTP sessions.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = "/*")
public class AuthFilter implements Filter {

    public static final String CSRF_TOKEN_SESSION_ATTR = "csrfToken";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Ensure session CSRF token exists
        HttpSession session = req.getSession(true);
        if (session.getAttribute(CSRF_TOKEN_SESSION_ATTR) == null) {
            session.setAttribute(CSRF_TOKEN_SESSION_ATTR, UUID.randomUUID().toString());
        }

        // Publicly allowed paths
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            // If AJAX request, return 401 Unauthorized
            String requestedWith = req.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equalsIgnoreCase(requestedWith) || path.startsWith("/api/")) {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session expired. Please log in again.");
                return;
            }
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Expose current user to request scope for easy EL expression access in JSPs
        req.setAttribute("currentUser", user);

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return path.equals("/") ||
                path.equals("/login") ||
                path.equals("/register") ||
                path.equals("/logout") ||
                path.equals("/simulator") ||
                path.startsWith("/static/") ||
                path.startsWith("/vr/") ||
                path.equals("/api/device/pair") ||
                path.equals("/api/telemetry") ||
                path.equals("/api/commands");
    }
}
