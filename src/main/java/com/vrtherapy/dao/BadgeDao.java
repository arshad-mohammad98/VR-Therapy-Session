package com.vrtherapy.dao;

import com.vrtherapy.model.Badge;

import java.util.List;

public interface BadgeDao extends GenericDao<Badge, Long> {

    List<Badge> findByPatientId(Long patientId);

    boolean hasBadge(Long patientId, String badgeType);
}
