package com.vrtherapy.service;

import com.vrtherapy.dto.SupportTicketDto;
import com.vrtherapy.dto.TicketReplyDto;
import com.vrtherapy.model.SupportTicket;
import com.vrtherapy.model.TicketPriority;
import com.vrtherapy.model.TicketReply;
import com.vrtherapy.model.TicketStatus;
import com.vrtherapy.model.User;
import java.util.List;

/**
 * Service contract managing technical support requests, priority triage, and ticketing responses.
 */
public interface SupportTicketService {

    /**
     * Submits a new support ticket.
     */
    SupportTicket createTicket(SupportTicketDto dto, User creator);

    /**
     * Adds an answer or update to an existing ticket.
     */
    TicketReply addReply(TicketReplyDto dto, User author);

    /**
     * Updates ticket resolution status.
     */
    SupportTicket updateTicketStatus(Long ticketId, TicketStatus newStatus, User updater);

    /**
     * Updates ticket priority level.
     */
    SupportTicket updateTicketPriority(Long ticketId, TicketPriority newPriority, User updater);

    /**
     * Retrieves ticket by ID.
     */
    SupportTicket getTicketById(Long id);

    /**
     * Retrieves all support tickets.
     */
    List<SupportTicket> getAllTickets();

    /**
     * Retrieves tickets submitted by a specific user.
     */
    List<SupportTicket> getTicketsByUser(User user);

    /**
     * Retrieves replies for a ticket ordered chronologically.
     */
    List<TicketReply> getRepliesForTicket(Long ticketId);

    /**
     * Counts open support tickets.
     */
    long countOpenTickets();
}
