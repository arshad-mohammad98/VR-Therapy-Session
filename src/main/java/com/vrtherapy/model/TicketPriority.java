package com.vrtherapy.model;

/**
 * Priority levels for technical support tickets.
 */
public enum TicketPriority {
    LOW("Low", "info"),
    MEDIUM("Medium", "primary"),
    HIGH("High", "warning"),
    URGENT("Urgent", "danger");

    private final String label;
    private final String badgeClass;

    TicketPriority(String label, String badgeClass) {
        this.label = label;
        this.badgeClass = badgeClass;
    }

    public String getLabel() {
        return label;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
