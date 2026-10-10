package com.vrtherapy.model;

/**
 * Lifecycle states of an assigned therapy session.
 */
public enum AssignmentStatus {
    ASSIGNED("Assigned", "badge-warning"),
    SCHEDULED("Scheduled", "badge-warning"),
    IN_PROGRESS("In Progress", "badge-primary"),
    COMPLETED("Completed", "badge-success"),
    CANCELLED("Cancelled", "badge-secondary");

    private final String label;
    private final String badgeClass;

    AssignmentStatus(String label, String badgeClass) {
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
