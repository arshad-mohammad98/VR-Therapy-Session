package com.vrtherapy.service.impl;

import com.google.gson.Gson;
import com.vrtherapy.dao.SessionDao;
import com.vrtherapy.dao.impl.SessionDaoImpl;
import com.vrtherapy.dto.TherapySessionDto;
import com.vrtherapy.exception.ResourceNotFoundException;
import com.vrtherapy.exception.ValidationException;
import com.vrtherapy.factory.VrEnvironmentFactory;
import com.vrtherapy.model.ConditionType;
import com.vrtherapy.model.EnvironmentType;
import com.vrtherapy.model.TherapySession;
import com.vrtherapy.model.User;
import com.vrtherapy.service.TherapySessionService;
import com.vrtherapy.strategy.SessionAdjustmentSuggestion;
import com.vrtherapy.util.ValidationUtil;
import com.vrtherapy.vrenvironment.VrEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Service implementation for VR Therapy Session protocols, WebXR scene generation,
 * and clinical validation.
 */
public class TherapySessionServiceImpl implements TherapySessionService {

    private static final Logger logger = LoggerFactory.getLogger(TherapySessionServiceImpl.class);
    private final SessionDao sessionDao;
    private final Gson gson = new Gson();

    public TherapySessionServiceImpl() {
        this(new SessionDaoImpl());
    }

    public TherapySessionServiceImpl(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    @Override
    public TherapySession createSession(TherapySessionDto dto, User therapist) {
        ValidationUtil.validateSessionParameters(dto.getTitle(), dto.getDefaultDurationMinutes(), dto.getIntensityLevel());

        ConditionType conditionType = dto.getConditionType() != null ? dto.getConditionType() : ConditionType.ANXIETY;
        EnvironmentType envType = dto.getEnvironmentType() != null ? dto.getEnvironmentType() : EnvironmentType.CALM_BEACH;

        Map<String, Object> sceneConfig = generateConfigurationMap(
                envType,
                dto.getIntensityLevel(),
                dto.isGuidedBreathingEnabled(),
                dto.isAudioEnabled()
        );

        TherapySession session = new TherapySession(
                dto.getTitle().trim(),
                conditionType,
                envType,
                dto.getDefaultDurationMinutes(),
                dto.getIntensityLevel(),
                dto.isAudioEnabled(),
                dto.isGuidedBreathingEnabled(),
                dto.getTherapistNotes(),
                therapist != null ? therapist.getId() : null
        );
        session.setConfigJson(gson.toJson(sceneConfig));

        return sessionDao.save(session);
    }

    @Override
    public TherapySession updateSession(Long id, TherapySessionDto dto, User therapist) {
        ValidationUtil.validateSessionParameters(dto.getTitle(), dto.getDefaultDurationMinutes(), dto.getIntensityLevel());

        TherapySession session = getSessionById(id);
        ConditionType conditionType = dto.getConditionType() != null ? dto.getConditionType() : ConditionType.ANXIETY;
        EnvironmentType envType = dto.getEnvironmentType() != null ? dto.getEnvironmentType() : EnvironmentType.CALM_BEACH;

        session.setTitle(dto.getTitle().trim());
        session.setConditionType(conditionType);
        session.setVrEnvironment(envType);
        session.setDefaultDurationMinutes(dto.getDefaultDurationMinutes());
        session.setIntensityLevel(dto.getIntensityLevel());
        session.setAudioEnabled(dto.isAudioEnabled());
        session.setGuidedBreathingEnabled(dto.isGuidedBreathingEnabled());
        session.setTherapistNotes(dto.getTherapistNotes());

        Map<String, Object> sceneConfig = generateConfigurationMap(
                envType,
                dto.getIntensityLevel(),
                dto.isGuidedBreathingEnabled(),
                dto.isAudioEnabled()
        );
        session.setConfigJson(gson.toJson(sceneConfig));

        sessionDao.update(session);
        return session;
    }

    @Override
    public TherapySession applySuggestionAdjustment(Long sessionId, SessionAdjustmentSuggestion suggestion, User therapist) {
        TherapySession session = getSessionById(sessionId);
        if (suggestion.getNewIntensity() != null) {
            session.setIntensityLevel(Math.max(1, Math.min(5, suggestion.getNewIntensity())));
        }
        if (suggestion.getNewDurationMinutes() != null) {
            session.setDefaultDurationMinutes(Math.max(5, Math.min(60, suggestion.getNewDurationMinutes())));
        }
        if (suggestion.getTargetGuidedBreathing() != null) {
            session.setGuidedBreathingEnabled(suggestion.getTargetGuidedBreathing());
        }
        if (suggestion.getTargetBackgroundAudio() != null) {
            session.setAudioEnabled(suggestion.getTargetBackgroundAudio());
        }

        // Re-generate scene configuration with updated parameters
        Map<String, Object> sceneConfig = generateConfigurationMap(
                session.getVrEnvironment(),
                session.getIntensityLevel(),
                session.isGuidedBreathingEnabled(),
                session.isAudioEnabled()
        );
        session.setConfigJson(gson.toJson(sceneConfig));
        sessionDao.update(session);
        logger.info("Applied adjustment suggestion to session {}: {}", sessionId, suggestion.getSuggestedAction());
        return session;
    }

    @Override
    public TherapySession getSessionById(Long id) {
        return sessionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Therapy session not found with ID: " + id));
    }

    @Override
    public List<TherapySession> getSessionsByTherapist(User therapist) {
        if (therapist == null) {
            return sessionDao.findAll();
        }
        return sessionDao.findByCreatedBy(therapist.getId());
    }

    @Override
    public List<TherapySession> getAllActiveSessions() {
        return sessionDao.findAll();
    }

    @Override
    public List<TherapySession> getAllSessions() {
        return sessionDao.findAll();
    }

    @Override
    public Map<String, Object> generateConfigurationMap(EnvironmentType envType, int intensity, boolean breathing, boolean audio) {
        VrEnvironment env = VrEnvironmentFactory.getEnvironment(envType);
        return env.generateSceneConfiguration(intensity, breathing, audio);
    }

    @Override
    public void deactivateSession(Long id, User therapist) {
        sessionDao.deleteById(id);
    }

    @Override
    public long countActiveSessions() {
        return sessionDao.count();
    }

    @Override
    public long countTotalSessions() {
        return sessionDao.count();
    }
}
