package com.vrtherapy.service;

import com.vrtherapy.dto.SessionAssignmentDto;
import com.vrtherapy.model.AssignmentStatus;
import com.vrtherapy.model.SessionAssignment;
import com.vrtherapy.model.User;
import java.util.List;

/**
 * Service contract managing the clinical assignment and scheduling of VR therapy sessions to patients.
 */
public interface SessionAssignmentService {

    /**
     * Creates and schedules an assignment for a patient.
     */
    SessionAssignment assignSession(SessionAssignmentDto dto, User therapist);

    /**
     * Retrieves an assignment by ID.
     */
    SessionAssignment getAssignmentById(Long id);

    /**
     * Updates assignment status (SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED).
     */
    SessionAssignment updateStatus(Long assignmentId, AssignmentStatus newStatus);

    /**
     * Cancels an assignment.
     */
    void cancelAssignment(Long assignmentId, User user);

    /**
     * Retrieves assignments for a patient ordered by scheduled time.
     */
    List<SessionAssignment> getAssignmentsForPatient(User patient);

    /**
     * Retrieves upcoming scheduled assignments for a patient.
     */
    List<SessionAssignment> getUpcomingAssignmentsForPatient(User patient);

    /**
     * Retrieves all assignments made by or associated with a therapist.
     */
    List<SessionAssignment> getAssignmentsForTherapist(User therapist);

    /**
     * Retrieves all assignments in the system.
     */
    List<SessionAssignment> getAllAssignments();

    /**
     * Counts assignments by status.
     */
    long countByStatus(AssignmentStatus status);

    /**
     * Counts total assignments.
     */
    long countTotalAssignments();
}
