package com.vrtherapy.service.impl;

import com.vrtherapy.dao.SessionResultDao;
import com.vrtherapy.dao.impl.SessionResultDaoImpl;
import com.vrtherapy.dto.SessionResultSubmissionDto;
import com.vrtherapy.exception.ResourceNotFoundException;
import com.vrtherapy.exception.ValidationException;
import com.vrtherapy.model.SessionResult;
import com.vrtherapy.model.User;
import com.vrtherapy.service.SessionResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for recording post-immersion session results and biometrics.
 */
public class SessionResultServiceImpl implements SessionResultService {

    private static final Logger logger = LoggerFactory.getLogger(SessionResultServiceImpl.class);
    private final SessionResultDao resultDao;

    public SessionResultServiceImpl() {
        this(new SessionResultDaoImpl());
    }

    public SessionResultServiceImpl(SessionResultDao resultDao) {
        this.resultDao = resultDao;
    }

    @Override
    public SessionResult recordResult(SessionResultSubmissionDto dto, User patient) {
        if (dto.getPreStressRating() < 1 || dto.getPreStressRating() > 10) {
            throw new ValidationException("Pre-session stress rating must be between 1 and 10.");
        }
        if (dto.getPostStressRating() < 1 || dto.getPostStressRating() > 10) {
            throw new ValidationException("Post-session stress rating must be between 1 and 10.");
        }

        SessionResult result = new SessionResult(
                dto.getSessionId(),
                patient.getId(),
                dto.getPreStressRating(),
                dto.getPostStressRating(),
                dto.getDurationSeconds(),
                dto.getAverageHeartRate() != null ? dto.getAverageHeartRate() : 75,
                dto.getPeakHeartRate() != null ? dto.getPeakHeartRate() : 85
        );
        result.setEmergencyStopped(dto.isEmergencyStopped());
        result.setStopReason(dto.getEmergencyStopReason());
        result.setCompletionNotes(dto.getClinicalNotes());

        SessionResult saved = resultDao.save(result);
        logger.info("Recorded session result #{} for patient {} (Delta: {})",
                saved.getId(), patient.getUsername(), saved.getStressDelta());
        return saved;
    }

    @Override
    public SessionResult getResultById(Long id) {
        return resultDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session result not found: " + id));
    }

    @Override
    public Optional<SessionResult> getResultByAssignmentId(Long assignmentId) {
        return resultDao.findById(assignmentId);
    }

    @Override
    public List<SessionResult> getResultsForPatient(User patient) {
        if (patient == null) {
            return List.of();
        }
        return resultDao.findByPatientId(patient.getId());
    }

    @Override
    public List<SessionResult> getResultsForSession(Long sessionId) {
        return resultDao.findBySessionId(sessionId);
    }

    @Override
    public List<SessionResult> getResultsForTherapist(User therapist) {
        return resultDao.findAll();
    }

    @Override
    public long countTotalCompletedResults() {
        return resultDao.count();
    }
}
