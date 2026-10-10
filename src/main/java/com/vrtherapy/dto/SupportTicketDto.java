package com.vrtherapy.dto;

import com.vrtherapy.model.TicketPriority;
import com.vrtherapy.model.TicketStatus;

/**
 * Data transfer object for raising and updating technical support tickets.
 */
public class SupportTicketDto {

    private Long id;

    private String subject;

    private String description;

    private TicketPriority priority = TicketPriority.MEDIUM;

    private TicketStatus status = TicketStatus.OPEN;

    public SupportTicketDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return subject;
    }

    public void setTitle(String title) {
        this.subject = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}
