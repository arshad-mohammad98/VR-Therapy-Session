package com.vrtherapy.dao;

import com.vrtherapy.model.LiveSession;
import com.vrtherapy.model.LiveSessionState;

import java.util.List;
import java.util.Optional;

public interface LiveSessionDao extends GenericDao<LiveSession, Long> {

    Optional<LiveSession> findActiveByPatientId(Long patientId);

    Optional<LiveSession> findActiveByTherapistId(Long therapistId);

    List<LiveSession> findByState(LiveSessionState state);

    void updateLiveParameters(Long liveSessionId, int heartRate, int stressScore, int elapsedSeconds);

    void updateState(Long liveSessionId, LiveSessionState state);

    void updateEnvironmentAndIntensity(Long liveSessionId, String environment, int intensity, boolean breathingGuide);

    void updateInVrMessage(Long liveSessionId, String message);
}
