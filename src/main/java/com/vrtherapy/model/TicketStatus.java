package com.vrtherapy.model;

/**
 * Resolution lifecycle states for technical support requests.
 */
public enum TicketStatus {
    OPEN("Open", "warning"),
    IN_PROGRESS("In Progress", "primary"),
    RESOLVED("Resolved", "success"),
    CLOSED("Closed", "secondary");

    private final String label;
    private final String badgeClass;

    TicketStatus(String label, String badgeClass) {
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
