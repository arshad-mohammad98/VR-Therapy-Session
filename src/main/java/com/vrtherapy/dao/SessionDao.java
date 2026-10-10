package com.vrtherapy.dao;

import com.vrtherapy.model.ConditionType;
import com.vrtherapy.model.TherapySession;

import java.util.List;

public interface SessionDao extends GenericDao<TherapySession, Long> {

    List<TherapySession> findByCreatedBy(Long createdBy);

    List<TherapySession> findByConditionType(ConditionType conditionType);
}
