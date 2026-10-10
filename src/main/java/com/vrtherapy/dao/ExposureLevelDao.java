package com.vrtherapy.dao;

import com.vrtherapy.model.ExposureLevel;

import java.util.List;

public interface ExposureLevelDao extends GenericDao<ExposureLevel, Long> {

    List<ExposureLevel> findByPlanIdOrderByLevelOrder(Long planId);

    void unlockLevel(Long levelId);
}
