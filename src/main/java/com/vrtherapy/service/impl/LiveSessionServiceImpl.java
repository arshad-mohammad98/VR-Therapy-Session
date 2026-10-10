package com.vrtherapy.service.impl;

import com.vrtherapy.dao.LiveSessionDao;
import com.vrtherapy.dao.impl.LiveSessionDaoImpl;
import com.vrtherapy.exception.InvalidStateTransitionException;
import com.vrtherapy.exception.ResourceNotFoundException;
import com.vrtherapy.model.EnvironmentType;
import com.vrtherapy.model.LiveSession;
import com.vrtherapy.model.LiveSessionState;
import com.vrtherapy.model.User;
import com.vrtherapy.observer.AlertManager;
import com.vrtherapy.service.LiveSessionService;
import com.vrtherapy.thread.CommandQueue;
import com.vrtherapy.thread.TelemetrySimulator;
import com.vrtherapy.thread.VrCommand;
import com.vrtherapy.thread.VrConnectionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

/**
 * Service implementation managing live remote VR sessions, state machine rules,
 * thread-safe command dispatch, and emergency stop procedures.
 *
 * Demonstrates:
 * - State Machine Pattern with transition enforcement
 * - Multithreading & Synchronization (CommandQueue, VrConnectionRegistry)
 * - Observer Pattern (AlertManager broadcasts)
 */
public class LiveSessionServiceImpl implements LiveSessionService {

    private static final Logger logger = LoggerFactory.getLogger(LiveSessionServiceImpl.class);
    private final LiveSessionDao liveSessionDao;
    private final ExecutorService telemetryExecutor;

    public LiveSessionServiceImpl() {
        this(new LiveSessionDaoImpl(), null);
    }

    public LiveSessionServiceImpl(LiveSessionDao liveSessionDao, ExecutorService telemetryExecutor) {
        this.liveSessionDao = liveSessionDao;
        this.telemetryExecutor = telemetryExecutor;
    }

    @Override
    public LiveSession createLiveSession(User therapist, Long patientId, Long sessionId, EnvironmentType environment, int intensity) {
        LiveSession session = new LiveSession(sessionId, patientId, therapist.getId(), environment, intensity);
        session.setState(LiveSessionState.SCHEDULED);
        session.setCurrentHeartRate(72);
        session.setCurrentStressScore(3);
        session.setBreathingGuideActive(true);

        LiveSession saved = liveSessionDao.save(session);
        logger.info("Live session #{} created by Therapist {} for Patient {}", saved.getId(), therapist.getUsername(), patientId);
        return saved;
    }

    @Override
    public LiveSession transitionState(Long sessionId, LiveSessionState targetState, String reason) {
        LiveSession session = getLiveSession(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Live session not found: " + sessionId));

        LiveSessionState currentState = session.getState();
        if (!currentState.canTransitionTo(targetState)) {
            String msg = "Illegal session transition from [" + currentState + "] to [" + targetState + "]";
            logger.warn(msg);
            throw new InvalidStateTransitionException(msg);
        }

        liveSessionDao.updateState(sessionId, targetState);
        session.setState(targetState);
        logger.info("Session #{} transitioned from {} -> {}. Reason: {}", sessionId, currentState, targetState, reason);

        if (targetState == LiveSessionState.ACTIVE && currentState != LiveSessionState.ACTIVE) {
            VrConnectionRegistry.getInstance().incrementActiveSessions();
            // Dispatch command to VR view
            CommandQueue.getInstance().enqueueCommand(sessionId, new VrCommand(VrCommand.CommandType.RESUME_SESSION, "Session Active"));

            // Start telemetry simulator thread if executor is available
            if (telemetryExecutor != null) {
                telemetryExecutor.submit(new TelemetrySimulator(sessionId, session.getPatientId()));
            }
        } else if (targetState == LiveSessionState.COMPLETED || targetState == LiveSessionState.EMERGENCY_STOPPED) {
            VrConnectionRegistry.getInstance().decrementActiveSessions();
            CommandQueue.getInstance().cleanupSession(sessionId);
        }

        return session;
    }

    @Override
    public LiveSession updateEnvironment(Long sessionId, EnvironmentType environment) {
        LiveSession session = getLiveSession(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Live session not found: " + sessionId));

        session.setCurrentEnvironment(environment);
        liveSessionDao.updateEnvironmentAndIntensity(sessionId, environment.name(), session.getCurrentIntensity(), session.isBreathingGuideActive());

        // Queue command for connected WebXR headset
        CommandQueue.getInstance().enqueueCommand(sessionId, new VrCommand(VrCommand.CommandType.CHANGE_ENVIRONMENT, environment.name()));
        logger.info("Session #{} environment updated to {}", sessionId, environment);
        return session;
    }

    @Override
    public LiveSession updateIntensity(Long sessionId, int intensity) {
        LiveSession session = getLiveSession(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Live session not found: " + sessionId));

        int clamped = Math.max(1, Math.min(5, intensity));
        session.setCurrentIntensity(clamped);
        liveSessionDao.updateEnvironmentAndIntensity(sessionId, session.getCurrentEnvironment().name(), clamped, session.isBreathingGuideActive());

        // Queue command for connected WebXR headset
        CommandQueue.getInstance().enqueueCommand(sessionId, new VrCommand(VrCommand.CommandType.CHANGE_INTENSITY, String.valueOf(clamped)));
        logger.info("Session #{} intensity adjusted to {}", sessionId, clamped);
        return session;
    }

    @Override
    public LiveSession toggleGuidedBreathing(Long sessionId, boolean active) {
        LiveSession session = getLiveSession(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Live session not found: " + sessionId));

        session.setBreathingGuideActive(active);
        liveSessionDao.updateEnvironmentAndIntensity(sessionId, session.getCurrentEnvironment().name(), session.getCurrentIntensity(), active);

        VrCommand.CommandType cmdType = active ? VrCommand.CommandType.START_BREATHING : VrCommand.CommandType.STOP_BREATHING;
        CommandQueue.getInstance().enqueueCommand(sessionId, new VrCommand(cmdType, active ? "ON" : "OFF"));
        logger.info("Session #{} breathing guide toggled: {}", sessionId, active);
        return session;
    }

    @Override
    public LiveSession sendVrPrompt(Long sessionId, String promptText) {
        LiveSession session = getLiveSession(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Live session not found: " + sessionId));

        session.setInVrMessage(promptText);
        liveSessionDao.updateInVrMessage(sessionId, promptText);

        // Queue message command to be rendered inside WebXR headset overlay
        CommandQueue.getInstance().enqueueCommand(sessionId, new VrCommand(VrCommand.CommandType.SHOW_MESSAGE, promptText));
        logger.info("Session #{} prompt dispatched: \"{}\"", sessionId, promptText);
        return session;
    }

    @Override
    public LiveSession triggerEmergencyStop(Long sessionId, String reason, String actorUsername) {
        LiveSession session = getLiveSession(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Live session not found: " + sessionId));

        liveSessionDao.updateState(sessionId, LiveSessionState.EMERGENCY_STOPPED);
        session.setState(LiveSessionState.EMERGENCY_STOPPED);

        // Enqueue high-priority emergency command to immediately disconnect headset immersion
        CommandQueue.getInstance().enqueueCommand(sessionId, new VrCommand(VrCommand.CommandType.EMERGENCY_STOP, reason != null ? reason : "Emergency stop triggered"));
        VrConnectionRegistry.getInstance().decrementActiveSessions();

        // Broadcast observer crisis alert
        AlertManager.getInstance().broadcastAlert(
                "EMERGENCY_STOP",
                "Emergency stop triggered for Session #" + sessionId + " by " + actorUsername + ". Reason: " + reason,
                session.getPatientId()
        );

        logger.warn("EMERGENCY STOP executed on Session #{} by {}. Reason: {}", sessionId, actorUsername, reason);
        return session;
    }

    @Override
    public Optional<LiveSession> getLiveSession(Long sessionId) {
        return liveSessionDao.findById(sessionId);
    }

    @Override
    public List<LiveSession> getSessionsForTherapist(User therapist) {
        return liveSessionDao.findAll();
    }

    @Override
    public List<LiveSession> getSessionsForPatient(User patient) {
        return liveSessionDao.findAll();
    }

    @Override
    public List<LiveSession> getActiveSessions() {
        return liveSessionDao.findByState(LiveSessionState.ACTIVE);
    }
}
