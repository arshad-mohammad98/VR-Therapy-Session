package com.vrtherapy.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "CentralErrorServlet", urlPatterns = {"/error"})
public class CentralErrorServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer statusCode = (Integer) req.getAttribute("jakarta.servlet.error.status_code");
        String message = (String) req.getAttribute("jakarta.servlet.error.message");
        Throwable throwable = (Throwable) req.getAttribute("jakarta.servlet.error.exception");

        if (statusCode == null && req.getParameter("code") != null) {
            try {
                statusCode = Integer.parseInt(req.getParameter("code"));
            } catch (NumberFormatException ignored) {}
        }
        req.setAttribute("statusCode", statusCode != null ? statusCode : 500);
        req.setAttribute("errorMessage", message != null ? message : (throwable != null ? throwable.getMessage() : "An unexpected server error occurred."));
        forward(req, resp, "error/general");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
