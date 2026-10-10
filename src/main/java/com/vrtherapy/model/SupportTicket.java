package com.vrtherapy.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain entity representing support requests submitted by users for technical or clinical assistance.
 */
public class SupportTicket extends BaseEntity {

    private Long createdBy;
    private String title;
    private String description;
    private TicketPriority priority = TicketPriority.MEDIUM;
    private TicketStatus status = TicketStatus.OPEN;
    private List<TicketReply> replies = new ArrayList<>();

    public SupportTicket() {
        super();
    }

    public SupportTicket(Long createdBy, String title, String description, TicketPriority priority) {
        super();
        this.createdBy = createdBy;
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public List<TicketReply> getReplies() {
        return replies;
    }

    public void setReplies(List<TicketReply> replies) {
        this.replies = replies;
    }
}
