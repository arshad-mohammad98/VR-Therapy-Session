package com.vrtherapy.dao;

import com.vrtherapy.model.SupportTicket;
import com.vrtherapy.model.TicketReply;
import com.vrtherapy.model.TicketStatus;

import java.util.List;

public interface TicketDao extends GenericDao<SupportTicket, Long> {

    List<SupportTicket> findByCreatedBy(Long createdBy);

    List<SupportTicket> findByStatus(TicketStatus status);

    void updateStatus(Long ticketId, TicketStatus status);

    void addReply(TicketReply reply);

    List<TicketReply> findRepliesByTicketId(Long ticketId);
}
