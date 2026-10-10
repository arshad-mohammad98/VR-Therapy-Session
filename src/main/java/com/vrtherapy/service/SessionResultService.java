package com.vrtherapy.service;

import com.vrtherapy.dto.SessionResultSubmissionDto;
import com.vrtherapy.model.SessionResult;
import com.vrtherapy.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Service contract for persisting and retrieving biometric telemetry and stress ratings from completed VR sessions.
 */
public interface SessionResultService {

    /**
     * Records session completion telemetry submitted from the WebXR interface.
     */
    SessionResult recordResult(SessionResultSubmissionDto dto, User patient);

    /**
     * Retrieves session result by ID.
     */
    SessionResult getResultById(Long id);

    /**
     * Retrieves result associated with a specific assignment.
     */
    Optional<SessionResult> getResultByAssignmentId(Long assignmentId);

    /**
     * Retrieves all results for a patient ordered chronologically.
     */
    List<SessionResult> getResultsForPatient(User patient);

    /**
     * Retrieves all results associated with a session template ID.
     */
    List<SessionResult> getResultsForSession(Long sessionId);

    /**
     * Retrieves all results supervised by a therapist.
     */
    List<SessionResult> getResultsForTherapist(User therapist);

    /**
     * Total number of completed therapy session results.
     */
    long countTotalCompletedResults();
}
