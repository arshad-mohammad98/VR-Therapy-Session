package com.vrtherapy.servlet;

import com.vrtherapy.dto.SupportTicketDto;
import com.vrtherapy.dto.TicketReplyDto;
import com.vrtherapy.model.Role;
import com.vrtherapy.model.SupportTicket;
import com.vrtherapy.model.TicketPriority;
import com.vrtherapy.model.TicketStatus;
import com.vrtherapy.model.User;
import com.vrtherapy.service.SupportTicketService;
import com.vrtherapy.service.impl.SupportTicketServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "TicketServlet", urlPatterns = {"/tickets"})
public class TicketServlet extends BaseServlet {

    private final SupportTicketService ticketService = new SupportTicketServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getCurrentUser(req);
        String ticketIdStr = req.getParameter("id");

        if (ticketIdStr != null && !ticketIdStr.isEmpty()) {
            SupportTicket ticket = ticketService.getTicketById(Long.parseLong(ticketIdStr));
            req.setAttribute("ticket", ticket);
            req.setAttribute("replies", ticketService.getRepliesForTicket(ticket.getId()));
            req.setAttribute("statuses", TicketStatus.values());
            forward(req, resp, "tickets/detail");
            return;
        }

        List<SupportTicket> list = (user.getRole() == Role.ADMIN)
                ? ticketService.getAllTickets()
                : ticketService.getTicketsByUser(user);

        req.setAttribute("tickets", list);
        req.setAttribute("priorities", TicketPriority.values());
        forward(req, resp, "tickets/list");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getCurrentUser(req);
        String action = req.getParameter("action");

        if ("create".equals(action)) {
            SupportTicketDto dto = new SupportTicketDto();
            dto.setTitle(req.getParameter("title"));
            dto.setDescription(req.getParameter("description"));
            dto.setPriority(TicketPriority.valueOf(req.getParameter("priority")));

            SupportTicket ticket = ticketService.createTicket(dto, user);
            resp.sendRedirect(req.getContextPath() + "/tickets?id=" + ticket.getId());
        } else if ("reply".equals(action)) {
            Long ticketId = Long.parseLong(req.getParameter("ticketId"));
            String message = req.getParameter("message");

            TicketReplyDto dto = new TicketReplyDto();
            dto.setTicketId(ticketId);
            dto.setMessage(message);

            ticketService.addReply(dto, user);
            resp.sendRedirect(req.getContextPath() + "/tickets?id=" + ticketId);
        } else if ("updateStatus".equals(action) && user.getRole() == Role.ADMIN) {
            Long ticketId = Long.parseLong(req.getParameter("ticketId"));
            TicketStatus status = TicketStatus.valueOf(req.getParameter("status"));

            ticketService.updateTicketStatus(ticketId, status, user);
            resp.sendRedirect(req.getContextPath() + "/tickets?id=" + ticketId);
        }
    }
}
