package com.vrtherapy.filter;

import com.vrtherapy.model.Role;
import com.vrtherapy.model.User;
import com.vrtherapy.service.SystemSettingService;
import com.vrtherapy.service.impl.SystemSettingServiceImpl;
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
 * Filter checking maintenance mode toggle from system settings.
 * Blocks non-administrative traffic when maintenance mode is engaged.
 */
@WebFilter(filterName = "MaintenanceModeFilter", urlPatterns = "/*")
public class MaintenanceModeFilter implements Filter {

    private final SystemSettingService systemSettingService = new SystemSettingServiceImpl();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Skip static assets, login, logout, and admin routes
        if (path.startsWith("/static/") || path.startsWith("/login") || path.startsWith("/logout") || path.startsWith("/admin")) {
            chain.doFilter(request, response);
            return;
        }

        if (systemSettingService.isMaintenanceModeEnabled()) {
            HttpSession session = req.getSession(false);
            User user = session != null ? (User) session.getAttribute("currentUser") : null;
            if (user == null || user.getRole() != Role.ADMIN) {
                res.setContentType("text/html;charset=UTF-8");
                res.getWriter().write("""
                    <!DOCTYPE html>
                    <html>
                    <head><title>System Maintenance - VR Therapy Platform</title>
                    <style>body{font-family:system-ui;background:#f0fdf4;color:#0f172a;display:flex;align-items:center;justify-content:center;height:100vh;margin:0;}
                    .box{background:white;padding:2.5rem;border-radius:1rem;box-shadow:0 10px 25px rgba(0,0,0,0.08);max-width:500px;text-align:center;}
                    h1{color:#0d9488;}p{color:#64748b;line-height:1.6;}</style></head>
                    <body><div class="box">
                    <h1>Scheduled System Maintenance</h1>
                    <p>The VR Therapy Platform is currently performing scheduled clinical calibrations and database maintenance.</p>
                    <p>Please check back shortly or contact your clinic administrator.</p>
                    </div></body></html>
                    """);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
