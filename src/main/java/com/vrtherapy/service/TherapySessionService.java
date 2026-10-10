package com.vrtherapy.service;

import com.vrtherapy.dto.TherapySessionDto;
import com.vrtherapy.model.ConditionType;
import com.vrtherapy.model.EnvironmentType;
import com.vrtherapy.model.TherapySession;
import com.vrtherapy.model.User;
import com.vrtherapy.strategy.SessionAdjustmentSuggestion;
import java.util.List;
import java.util.Map;

/**
 * Service contract managing VR therapy session template creation, parameter validation,
 * procedural WebXR environment scene generation, and clinical protocol adjustments.
 */
public interface TherapySessionService {

    /**
     * Creates and saves a new TherapySession, generating the JSON scene configuration.
     */
    TherapySession createSession(TherapySessionDto dto, User therapist);

    /**
     * Updates an existing session template and regenerates its JSON scene configuration.
     */
    TherapySession updateSession(Long id, TherapySessionDto dto, User therapist);

    /**
     * Applies a clinical suggestion (e.g. intensity change, duration adjustment) directly to the session.
     */
    TherapySession applySuggestionAdjustment(Long sessionId, SessionAdjustmentSuggestion suggestion, User therapist);

    /**
     * Retrieves a session entity by its ID.
     */
    TherapySession getSessionById(Long id);

    /**
     * Retrieves all sessions designed by a specific therapist.
     */
    List<TherapySession> getSessionsByTherapist(User therapist);

    /**
     * Retrieves all active sessions.
     */
    List<TherapySession> getAllActiveSessions();

    /**
     * Retrieves all sessions regardless of active state.
     */
    List<TherapySession> getAllSessions();

    /**
     * Generates a Map-based configuration using the VrEnvironmentFactory polymorphism.
     */
    Map<String, Object> generateConfigurationMap(EnvironmentType envType, int intensity, boolean breathing, boolean audio);

    /**
     * Soft-deletes / deactivates a session.
     */
    void deactivateSession(Long id, User therapist);

    /**
     * Returns total active sessions count.
     */
    long countActiveSessions();

    /**
     * Returns total sessions count.
     */
    long countTotalSessions();
}
