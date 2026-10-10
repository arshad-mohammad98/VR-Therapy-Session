package com.vrtherapy.model;

import java.time.LocalDateTime;

/**
 * Domain entity representing direct asynchronous communication between patient and therapist.
 */
public class Message extends BaseEntity {

    private Long senderId;
    private Long recipientId;
    private String content;
    private boolean read = false;
    private LocalDateTime sentAt;

    public Message() {
        super();
        this.sentAt = LocalDateTime.now();
    }

    public Message(Long senderId, Long recipientId, String content) {
        super();
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.content = content;
        this.sentAt = LocalDateTime.now();
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
