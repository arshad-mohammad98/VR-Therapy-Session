package com.vrtherapy.dao;

import com.vrtherapy.model.Feedback;

import java.util.List;
import java.util.Optional;

public interface FeedbackDao extends GenericDao<Feedback, Long> {

    Optional<Feedback> findByResultId(Long resultId);

    List<Feedback> findByPatientId(Long patientId);

    List<Feedback> findBySessionId(Long sessionId);

    void updateAppliedAdjustment(Long feedbackId, String adjustment);
}
