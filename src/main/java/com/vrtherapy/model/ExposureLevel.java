package com.vrtherapy.model;

/**
 * Domain entity representing an ordered tier within an exposure hierarchy.
 * Next tier unlocks only when patient distress at the current tier falls below threshold.
 */
public class ExposureLevel extends BaseEntity {

    private Long planId;
    private Long sessionId;
    private int levelOrder;
    private String title;
    private String description;
    private int distressRatingThreshold = 5;
    private boolean unlocked = false;
    private String notes;

    public ExposureLevel() {
        super();
    }

    public ExposureLevel(Long planId, Long sessionId, int levelOrder, String title,
                         String description, int distressRatingThreshold, boolean unlocked, String notes) {
        super();
        this.planId = planId;
        this.sessionId = sessionId;
        this.levelOrder = levelOrder;
        this.title = title;
        this.description = description;
        this.distressRatingThreshold = distressRatingThreshold;
        this.unlocked = unlocked;
        this.notes = notes;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public int getLevelOrder() {
        return levelOrder;
    }

    public void setLevelOrder(int levelOrder) {
        this.levelOrder = levelOrder;
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

    public int getDistressRatingThreshold() {
        return distressRatingThreshold;
    }

    public void setDistressRatingThreshold(int distressRatingThreshold) {
        this.distressRatingThreshold = distressRatingThreshold;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
