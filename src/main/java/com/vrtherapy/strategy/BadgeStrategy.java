package com.vrtherapy.strategy;

import com.vrtherapy.model.Badge;

/**
 * Strategy interface specifying criteria and badge creation for patient therapeutic achievements.
 */
public interface BadgeStrategy {

    String getBadgeCode();

    String getBadgeName();

    String getDescription();

    String getIcon();

    boolean qualifies(BadgeContext context);

    default Badge createBadge(BadgeContext context) {
        Long pid = context.getPatient() != null ? context.getPatient().getId() : null;
        return new Badge(pid, getBadgeCode(), getBadgeName(), getDescription(), getIcon());
    }
}
