package com.vrtherapy.dao;

import com.vrtherapy.model.AssignmentStatus;
import com.vrtherapy.model.SessionAssignment;

import java.util.List;

public interface SessionAssignmentDao extends GenericDao<SessionAssignment, Long> {

    List<SessionAssignment> findByPatientId(Long patientId);

    List<SessionAssignment> findByPatientIdAndStatus(Long patientId, AssignmentStatus status);

    void updateStatus(Long assignmentId, AssignmentStatus status);
}
