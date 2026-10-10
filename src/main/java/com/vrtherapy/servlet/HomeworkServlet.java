package com.vrtherapy.servlet;

import com.vrtherapy.model.Homework;
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
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "HomeworkServlet", urlPatterns = {"/patient/homework", "/therapist/homework"})
public class HomeworkServlet extends BaseServlet {

    private final WellbeingService wellbeingService = new WellbeingServiceImpl();
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getCurrentUser(req);
        if (user.getRole() == Role.THERAPIST || user.getRole() == Role.ADMIN) {
            req.setAttribute("patients", userService.getPatientsByTherapist(user.getId()));
            req.setAttribute("homeworkList", wellbeingService.getHomeworkAssignedByTherapist(user));
            forward(req, resp, "therapist/homework");
        } else {
            req.setAttribute("homeworkList", wellbeingService.getHomeworkForPatient(user));
            forward(req, resp, "patient/homework");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getCurrentUser(req);
        String action = req.getParameter("action");

        if ("assign".equals(action)) {
            Long patientId = Long.parseLong(req.getParameter("patientId"));
            String title = req.getParameter("title");
            String description = req.getParameter("description");
            String category = req.getParameter("category");
            LocalDate dueDate = LocalDate.parse(req.getParameter("dueDate"));

            wellbeingService.assignHomework(user, patientId, title, description, category, dueDate);
            resp.sendRedirect(req.getContextPath() + "/therapist/homework?assigned=true");
        } else if ("complete".equals(action)) {
            Long homeworkId = Long.parseLong(req.getParameter("homeworkId"));
            String reflection = req.getParameter("reflection");

            wellbeingService.completeHomework(homeworkId, user, reflection);
            resp.sendRedirect(req.getContextPath() + "/patient/homework?completed=true");
        }
    }
}
