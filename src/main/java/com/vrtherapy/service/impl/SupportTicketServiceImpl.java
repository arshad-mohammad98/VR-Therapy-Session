package com.vrtherapy.service.impl;

import com.vrtherapy.dao.TicketDao;
import com.vrtherapy.dao.impl.TicketDaoImpl;
import com.vrtherapy.dto.SupportTicketDto;
import com.vrtherapy.dto.TicketReplyDto;
import com.vrtherapy.exception.ResourceNotFoundException;
import com.vrtherapy.exception.ValidationException;
import com.vrtherapy.model.SupportTicket;
import com.vrtherapy.model.TicketPriority;
import com.vrtherapy.model.TicketReply;
import com.vrtherapy.model.TicketStatus;
import com.vrtherapy.model.User;
import com.vrtherapy.service.SupportTicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SupportTicketServiceImpl implements SupportTicketService {

    private static final Logger logger = LoggerFactory.getLogger(SupportTicketServiceImpl.class);
    private final TicketDao ticketDao;

    public SupportTicketServiceImpl() {
        this(new TicketDaoImpl());
    }

    public SupportTicketServiceImpl(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }

    @Override
    public SupportTicket createTicket(SupportTicketDto dto, User creator) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new ValidationException("Ticket title cannot be empty.");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            throw new ValidationException("Ticket description cannot be empty.");
        }

        TicketPriority priority = dto.getPriority() != null ? dto.getPriority() : TicketPriority.MEDIUM;
        SupportTicket ticket = new SupportTicket(creator.getId(), dto.getTitle().trim(), dto.getDescription().trim(), priority);
        ticket.setStatus(TicketStatus.OPEN);

        SupportTicket saved = ticketDao.save(ticket);
        logger.info("Created support ticket #{} ('{}') by user {}", saved.getId(), saved.getTitle(), creator.getUsername());
        return saved;
    }

    @Override
    public TicketReply addReply(TicketReplyDto dto, User author) {
        if (dto.getMessage() == null || dto.getMessage().trim().isEmpty()) {
            throw new ValidationException("Reply message cannot be empty.");
        }

        TicketReply reply = new TicketReply(dto.getTicketId(), author.getId(), dto.getMessage().trim());
        reply.setAuthorName(author.getFullName());
        ticketDao.addReply(reply);

        // Advance ticket to IN_PROGRESS if previously OPEN
        SupportTicket ticket = getTicketById(dto.getTicketId());
        if (ticket.getStatus() == TicketStatus.OPEN) {
            ticketDao.updateStatus(ticket.getId(), TicketStatus.IN_PROGRESS);
        }

        logger.info("Added reply to ticket #{} by user {}", dto.getTicketId(), author.getUsername());
        return reply;
    }

    @Override
    public SupportTicket updateTicketStatus(Long ticketId, TicketStatus newStatus, User updater) {
        SupportTicket ticket = getTicketById(ticketId);
        ticket.setStatus(newStatus);
        ticketDao.updateStatus(ticketId, newStatus);
        logger.info("Ticket #{} status updated to {} by {}", ticketId, newStatus, updater.getUsername());
        return ticket;
    }

    @Override
    public SupportTicket updateTicketPriority(Long ticketId, TicketPriority newPriority, User updater) {
        SupportTicket ticket = getTicketById(ticketId);
        ticket.setPriority(newPriority);
        ticketDao.update(ticket);
        logger.info("Ticket #{} priority updated to {} by {}", ticketId, newPriority, updater.getUsername());
        return ticket;
    }

    @Override
    public SupportTicket getTicketById(Long id) {
        return ticketDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Support ticket not found: " + id));
    }

    @Override
    public List<SupportTicket> getAllTickets() {
        return ticketDao.findAll();
    }

    @Override
    public List<SupportTicket> getTicketsByUser(User user) {
        if (user == null) {
            return List.of();
        }
        return ticketDao.findByCreatedBy(user.getId());
    }

    @Override
    public List<TicketReply> getRepliesForTicket(Long ticketId) {
        return ticketDao.findRepliesByTicketId(ticketId);
    }

    @Override
    public long countOpenTickets() {
        return ticketDao.findByStatus(TicketStatus.OPEN).size() + ticketDao.findByStatus(TicketStatus.IN_PROGRESS).size();
    }
}
