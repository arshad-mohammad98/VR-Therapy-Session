package com.vrtherapy.model;

import java.time.LocalDateTime;

/**
 * Domain entity representing clinical milestones and adherence badges earned by patients.
 */
public class Badge extends BaseEntity {

    private Long patientId;
    private String badgeType;
    private String title;
    private String description;
    private String iconName;
    private LocalDateTime awardedAt;

    public Badge() {
        super();
        this.awardedAt = LocalDateTime.now();
    }

    public Badge(Long patientId, String badgeType, String title, String description, String iconName) {
        super();
        this.patientId = patientId;
        this.badgeType = badgeType;
        this.title = title;
        this.description = description;
        this.iconName = iconName;
        this.awardedAt = LocalDateTime.now();
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(String badgeType) {
        this.badgeType = badgeType;
    }

    public String getBadgeCode() {
        return badgeType;
    }

    public void setBadgeCode(String badgeCode) {
        this.badgeType = badgeCode;
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

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public LocalDateTime getAwardedAt() {
        return awardedAt;
    }

    public void setAwardedAt(LocalDateTime awardedAt) {
        this.awardedAt = awardedAt;
    }
}
