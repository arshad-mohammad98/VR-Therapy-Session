package com.vrtherapy.dto;


/**
 * Data transfer object for adding messages and administrative answers to a support ticket.
 */
public class TicketReplyDto {

    private Long ticketId;

    private String message;

    public TicketReplyDto() {
    }

    public TicketReplyDto(Long ticketId, String message) {
        this.ticketId = ticketId;
        this.message = message;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
