package com.vrtherapy.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrtherapy.filter.AuthFilter;
import com.vrtherapy.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Base abstract servlet providing common MVC forwarding, JSON serialization (Gson),
 * session retrieval, and CSRF token verification.
 *
 * Demonstrates:
 * - OOP Abstraction and inheritance across Servlet controllers
 * - Clean centralized JSON rendering and error responses
 */
public abstract class BaseServlet extends HttpServlet {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .serializeNulls()
            .create();

    protected User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("currentUser");
        }
        return null;
    }

    protected void forward(HttpServletRequest request, HttpServletResponse response, String viewName)
            throws ServletException, IOException {
        String path = "/WEB-INF/views/" + viewName + ".jsp";
        request.getRequestDispatcher(path).forward(request, response);
    }

    protected void sendJson(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.write(gson.toJson(data));
            out.flush();
        }
    }

    protected void sendErrorJson(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> err = new HashMap<>();
        err.put("success", false);
        err.put("error", message);
        err.put("status", statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.write(gson.toJson(err));
            out.flush();
        }
    }

    protected boolean validateCsrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        String sessionToken = (String) session.getAttribute(AuthFilter.CSRF_TOKEN_SESSION_ATTR);
        String formToken = request.getParameter("csrfToken");
        if (formToken == null) {
            formToken = request.getHeader("X-CSRF-Token");
        }
        return sessionToken != null && sessionToken.equals(formToken);
    }
}
