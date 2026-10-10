package com.vrtherapy.service.impl;

import com.vrtherapy.dao.SessionAssignmentDao;
import com.vrtherapy.dao.impl.SessionAssignmentDaoImpl;
import com.vrtherapy.dto.SessionAssignmentDto;
import com.vrtherapy.exception.ResourceNotFoundException;
import com.vrtherapy.exception.UnauthorizedAccessException;
import com.vrtherapy.model.AssignmentStatus;
import com.vrtherapy.model.Role;
import com.vrtherapy.model.SessionAssignment;
import com.vrtherapy.model.User;
import com.vrtherapy.service.SessionAssignmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

public class SessionAssignmentServiceImpl implements SessionAssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(SessionAssignmentServiceImpl.class);
    private final SessionAssignmentDao assignmentDao;

    public SessionAssignmentServiceImpl() {
        this(new SessionAssignmentDaoImpl());
    }

    public SessionAssignmentServiceImpl(SessionAssignmentDao assignmentDao) {
        this.assignmentDao = assignmentDao;
    }

    @Override
    public SessionAssignment assignSession(SessionAssignmentDto dto, User therapist) {
        LocalDate date = dto.getScheduledTime() != null ? dto.getScheduledTime().toLocalDate() : LocalDate.now();
        SessionAssignment assignment = new SessionAssignment(
                dto.getPatientId(),
                dto.getTherapySessionId(),
                null,
                AssignmentStatus.SCHEDULED,
                date
        );
        SessionAssignment saved = assignmentDao.save(assignment);
        logger.info("Assigned session #{} to patient ID {} by therapist {}",
                dto.getTherapySessionId(), dto.getPatientId(), therapist != null ? therapist.getUsername() : "system");
        return saved;
    }

    @Override
    public SessionAssignment getAssignmentById(Long id) {
        return assignmentDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with ID: " + id));
    }

    @Override
    public SessionAssignment updateStatus(Long assignmentId, AssignmentStatus newStatus) {
        SessionAssignment assignment = getAssignmentById(assignmentId);
        assignment.setStatus(newStatus);
        assignmentDao.updateStatus(assignmentId, newStatus);
        logger.info("Assignment #{} status updated to {}", assignmentId, newStatus);
        return assignment;
    }

    @Override
    public void cancelAssignment(Long assignmentId, User user) {
        SessionAssignment assignment = getAssignmentById(assignmentId);
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.THERAPIST) {
            throw new UnauthorizedAccessException("Only clinicians or administrators may cancel assigned sessions.");
        }
        assignment.setStatus(AssignmentStatus.CANCELLED);
        assignmentDao.updateStatus(assignmentId, AssignmentStatus.CANCELLED);
        logger.info("Assignment #{} cancelled by user {}", assignmentId, user.getUsername());
    }

    @Override
    public List<SessionAssignment> getAssignmentsForPatient(User patient) {
        if (patient == null) {
            return List.of();
        }
        return assignmentDao.findByPatientId(patient.getId());
    }

    @Override
    public List<SessionAssignment> getUpcomingAssignmentsForPatient(User patient) {
        if (patient == null) {
            return List.of();
        }
        return assignmentDao.findByPatientIdAndStatus(patient.getId(), AssignmentStatus.SCHEDULED);
    }

    @Override
    public List<SessionAssignment> getAssignmentsForTherapist(User therapist) {
        return assignmentDao.findAll();
    }

    @Override
    public List<SessionAssignment> getAllAssignments() {
        return assignmentDao.findAll();
    }

    @Override
    public long countByStatus(AssignmentStatus status) {
        return assignmentDao.findAll().stream().filter(a -> a.getStatus() == status).count();
    }

    @Override
    public long countTotalAssignments() {
        return assignmentDao.count();
    }
}
