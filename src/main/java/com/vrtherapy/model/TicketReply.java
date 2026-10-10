package com.vrtherapy.model;

import java.time.LocalDateTime;

/**
 * Domain entity representing conversational responses inside a support ticket thread.
 */
public class TicketReply extends BaseEntity {

    private Long ticketId;
    private Long authorId;
    private String authorName;
    private String message;
    private LocalDateTime createdAt;

    public TicketReply() {
        super();
        this.createdAt = LocalDateTime.now();
    }

    public TicketReply(Long ticketId, Long authorId, String message) {
        super();
        this.ticketId = ticketId;
        this.authorId = authorId;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
