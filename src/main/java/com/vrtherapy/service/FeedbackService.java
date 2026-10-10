package com.vrtherapy.service;

import com.vrtherapy.dto.FeedbackSubmissionDto;
import com.vrtherapy.dto.FeedbackSummaryDto;
import com.vrtherapy.model.Feedback;
import com.vrtherapy.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Service contract governing patient subjective feedback collection, sentiment summarization,
 * and clinical review flags.
 */
public interface FeedbackService {

    /**
     * Submits patient feedback for an assignment.
     */
    Feedback submitFeedback(FeedbackSubmissionDto dto, User patient);

    /**
     * Retrieves feedback by ID.
     */
    Feedback getFeedbackById(Long id);

    /**
     * Retrieves feedback for a specific assignment.
     */
    Optional<Feedback> getFeedbackByAssignmentId(Long assignmentId);

    /**
     * Retrieves all feedbacks submitted by a patient.
     */
    List<Feedback> getFeedbacksByPatient(User patient);

    /**
     * Retrieves all feedbacks for a session template.
     */
    List<Feedback> getFeedbacksForSession(Long sessionId);

    /**
     * Retrieves all feedbacks for sessions created by a therapist.
     */
    List<Feedback> getFeedbacksForTherapist(User therapist);

    /**
     * Generates an analytical summary of feedbacks for a session, including average rating and common feedback themes.
     */
    FeedbackSummaryDto getFeedbackSummaryForSession(Long sessionId);

    /**
     * Marks feedback as reviewed by the therapist.
     */
    Feedback markReviewed(Long feedbackId, String therapistNotes, User therapist);
}
