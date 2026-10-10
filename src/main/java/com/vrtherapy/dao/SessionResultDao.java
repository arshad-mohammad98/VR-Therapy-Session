package com.vrtherapy.dao;

import com.vrtherapy.model.SessionResult;

import java.util.List;

public interface SessionResultDao extends GenericDao<SessionResult, Long> {

    List<SessionResult> findByPatientId(Long patientId);

    List<SessionResult> findBySessionId(Long sessionId);

    List<SessionResult> findRecentByPatientId(Long patientId, int limit);
}
