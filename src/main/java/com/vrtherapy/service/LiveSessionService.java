package com.vrtherapy.service;

import com.vrtherapy.model.EnvironmentType;
import com.vrtherapy.model.LiveSession;
import com.vrtherapy.model.LiveSessionState;
import com.vrtherapy.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Service managing live remote-guided clinical VR sessions, real-time command routing,
 * and safety-enforced finite state machine transitions.
 */
public interface LiveSessionService {

    LiveSession createLiveSession(User therapist, Long patientId, Long assignmentId, EnvironmentType environment, int intensity);

    LiveSession transitionState(Long sessionId, LiveSessionState targetState, String reason);

    LiveSession updateEnvironment(Long sessionId, EnvironmentType environment);

    LiveSession updateIntensity(Long sessionId, int intensity);

    LiveSession toggleGuidedBreathing(Long sessionId, boolean active);

    LiveSession sendVrPrompt(Long sessionId, String promptText);

    LiveSession triggerEmergencyStop(Long sessionId, String reason, String actorUsername);

    Optional<LiveSession> getLiveSession(Long sessionId);

    List<LiveSession> getSessionsForTherapist(User therapist);

    List<LiveSession> getSessionsForPatient(User patient);

    List<LiveSession> getActiveSessions();
}
