package com.vrtherapy.service.impl;

import com.vrtherapy.dao.FeedbackDao;
import com.vrtherapy.dao.SessionDao;
import com.vrtherapy.dao.impl.FeedbackDaoImpl;
import com.vrtherapy.dao.impl.SessionDaoImpl;
import com.vrtherapy.dto.FeedbackSubmissionDto;
import com.vrtherapy.dto.FeedbackSummaryDto;
import com.vrtherapy.exception.ResourceNotFoundException;
import com.vrtherapy.exception.ValidationException;
import com.vrtherapy.model.Feedback;
import com.vrtherapy.model.User;
import com.vrtherapy.service.FeedbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for feedback collection, comfort rating analytics,
 * and therapist feedback review workflows.
 */
public class FeedbackServiceImpl implements FeedbackService {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackServiceImpl.class);

    private final FeedbackDao feedbackDao;
    private final SessionDao sessionDao;

    public FeedbackServiceImpl() {
        this(new FeedbackDaoImpl(), new SessionDaoImpl());
    }

    public FeedbackServiceImpl(FeedbackDao feedbackDao, SessionDao sessionDao) {
        this.feedbackDao = feedbackDao;
        this.sessionDao = sessionDao;
    }

    @Override
    public Feedback submitFeedback(FeedbackSubmissionDto dto, User patient) {
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new ValidationException("Rating must be between 1 and 5.");
        }
        if (dto.getComfortScore() < 1 || dto.getComfortScore() > 5) {
            throw new ValidationException("Comfort score must be between 1 and 5.");
        }
        if (dto.getNauseaScore() < 1 || dto.getNauseaScore() > 5) {
            throw new ValidationException("Nausea score must be between 1 and 5.");
        }

        Feedback fb = new Feedback(
                dto.getAssignmentId(), // result / assignment link
                patient.getId(),
                dto.getSessionId(),
                dto.getRating(),
                dto.getComfortScore(),
                dto.getNauseaScore(),
                dto.getComments()
        );

        Feedback saved = feedbackDao.save(fb);
        logger.info("Submitted feedback #{} from patient {}", saved.getId(), patient.getUsername());
        return saved;
    }

    @Override
    public Feedback getFeedbackById(Long id) {
        return feedbackDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found: " + id));
    }

    @Override
    public Optional<Feedback> getFeedbackByAssignmentId(Long assignmentId) {
        return feedbackDao.findByResultId(assignmentId);
    }

    @Override
    public List<Feedback> getFeedbacksByPatient(User patient) {
        if (patient == null) {
            return List.of();
        }
        return feedbackDao.findByPatientId(patient.getId());
    }

    @Override
    public List<Feedback> getFeedbacksForSession(Long sessionId) {
        return feedbackDao.findBySessionId(sessionId);
    }

    @Override
    public List<Feedback> getFeedbacksForTherapist(User therapist) {
        return feedbackDao.findAll();
    }

    @Override
    public FeedbackSummaryDto getFeedbackSummaryForSession(Long sessionId) {
        List<Feedback> list = feedbackDao.findBySessionId(sessionId);
        FeedbackSummaryDto summary = new FeedbackSummaryDto();
        summary.setSessionId(sessionId);
        summary.setTotalFeedbacks((long) list.size());

        if (list.isEmpty()) {
            summary.setAverageRating(0.0);
            summary.setAverageComfort(0.0);
            summary.setAverageNausea(0.0);
            return summary;
        }

        double avgRating = list.stream().mapToInt(Feedback::getRating).average().orElse(0.0);
        double avgComfort = list.stream().mapToInt(Feedback::getComfortScore).average().orElse(0.0);
        double avgNausea = list.stream().mapToInt(Feedback::getNauseaScore).average().orElse(0.0);

        summary.setAverageRating(Math.round(avgRating * 10.0) / 10.0);
        summary.setAverageComfort(Math.round(avgComfort * 10.0) / 10.0);
        summary.setAverageNausea(Math.round(avgNausea * 10.0) / 10.0);
        return summary;
    }

    @Override
    public Feedback markReviewed(Long feedbackId, String therapistNotes, User therapist) {
        Feedback fb = getFeedbackById(feedbackId);
        fb.setAppliedAdjustment("Reviewed by " + therapist.getFullName() + ": " + therapistNotes);
        feedbackDao.updateAppliedAdjustment(feedbackId, fb.getAppliedAdjustment());
        return fb;
    }
}
