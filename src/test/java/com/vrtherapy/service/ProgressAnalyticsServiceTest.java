package com.vrtherapy.service;

import com.vrtherapy.dao.FeedbackDao;
import com.vrtherapy.dao.SessionAssignmentDao;
import com.vrtherapy.dao.SessionResultDao;
import com.vrtherapy.dto.PatientProgressDto;
import com.vrtherapy.model.*;
import com.vrtherapy.service.impl.ProgressAnalyticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressAnalyticsServiceTest {

    @Mock
    private SessionAssignmentDao assignmentDao;

    @Mock
    private SessionResultDao resultDao;

    @Mock
    private FeedbackDao feedbackDao;

    @InjectMocks
    private ProgressAnalyticsServiceImpl analyticsService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient("patient1", "pass", "Sarah Connor", "sarah@example.com", true);
        patient.setId(10L);
    }

    @Test
    @DisplayName("Should return baseline metrics when patient has zero completed sessions")
    void testCalculatePatientProgressWithNoResults() {
        when(assignmentDao.findByPatientId(10L)).thenReturn(Collections.emptyList());
        when(resultDao.findByPatientId(10L)).thenReturn(Collections.emptyList());
        when(feedbackDao.findByPatientId(10L)).thenReturn(Collections.emptyList());

        PatientProgressDto progress = analyticsService.calculatePatientProgress(patient);

        assertNotNull(progress);
        assertEquals(0, progress.getTotalAssigned());
        assertEquals(0, progress.getTotalCompleted());
        assertEquals(0.0, progress.getCompletionRate());
        assertEquals(0.0, progress.getAveragePreStress());
        assertEquals(0.0, progress.getAveragePostStress());
        assertEquals("Baseline Data Gathering", progress.getOverallTrend());
    }

    @Test
    @DisplayName("Should compute correct stress delta and improvement percentage")
    void testStressDeltaAndImprovementPercentage() {
        SessionAssignment assignment1 = new SessionAssignment(1L, 10L, LocalDateTime.now(), "Notes");
        assignment1.setStatus(AssignmentStatus.COMPLETED);

        SessionAssignment assignment2 = new SessionAssignment(2L, 10L, LocalDateTime.now(), "Notes");
        assignment2.setStatus(AssignmentStatus.COMPLETED);

        SessionResult result1 = new SessionResult(1L, 10L, 8, 4, 1200, 75, 85);
        result1.setCompletedAt(LocalDateTime.now().minusDays(2));

        SessionResult result2 = new SessionResult(2L, 10L, 6, 2, 1200, 70, 80);
        result2.setCompletedAt(LocalDateTime.now().minusDays(1));

        Feedback feedback1 = new Feedback(1L, 10L, 1L, 5, 5, 1, "Great");
        Feedback feedback2 = new Feedback(2L, 10L, 2L, 4, 4, 2, "Good");

        when(assignmentDao.findByPatientId(10L)).thenReturn(List.of(assignment1, assignment2));
        when(resultDao.findByPatientId(10L)).thenReturn(List.of(result1, result2));
        when(feedbackDao.findByPatientId(10L)).thenReturn(List.of(feedback1, feedback2));

        PatientProgressDto progress = analyticsService.calculatePatientProgress(patient);

        assertEquals(2, progress.getTotalAssigned());
        assertEquals(2, progress.getTotalCompleted());
        assertEquals(100.0, progress.getCompletionRate());
        assertEquals(7.0, progress.getAveragePreStress()); // (8 + 6) / 2
        assertEquals(3.0, progress.getAveragePostStress()); // (4 + 2) / 2
        assertEquals(4.0, progress.getAverageStressDelta()); // 7.0 - 3.0 = 4.0
        assertTrue(progress.getImprovementPercentage() > 50.0);
        assertEquals(4.5, progress.getAverageRating()); // (5 + 4) / 2
        assertEquals(72.5, progress.getAverageHeartRate()); // (75 + 70) / 2
        assertTrue(progress.getOverallTrend().contains("Marked Relaxation"));
    }

    @Test
    @DisplayName("Should flag elevated reactivity when post-session stress is worse than pre-session")
    void testClinicalTrendClassificationElevatedStress() {
        SessionAssignment assignment = new SessionAssignment(1L, 10L, LocalDateTime.now(), "Notes");
        assignment.setStatus(AssignmentStatus.COMPLETED);

        // Pre: 4, Post: 8 (Stress increased!)
        SessionResult result = new SessionResult(1L, 10L, 4, 8, 900, 98, 120);
        result.setCompletedAt(LocalDateTime.now());

        when(assignmentDao.findByPatientId(10L)).thenReturn(List.of(assignment));
        when(resultDao.findByPatientId(10L)).thenReturn(List.of(result));
        when(feedbackDao.findByPatientId(10L)).thenReturn(Collections.emptyList());

        PatientProgressDto progress = analyticsService.calculatePatientProgress(patient);

        assertTrue(progress.getAverageStressDelta() < 0);
        assertTrue(progress.getOverallTrend().contains("Elevated Reactivity"));
    }

    @Test
    @DisplayName("Should calculate correct completion rate when some assignments are pending")
    void testCompletionRateCalculation() {
        SessionAssignment completed = new SessionAssignment(1L, 10L, LocalDateTime.now(), "Notes");
        completed.setStatus(AssignmentStatus.COMPLETED);

        SessionAssignment pending = new SessionAssignment(2L, 10L, LocalDateTime.now().plusDays(1), "Notes");
        pending.setStatus(AssignmentStatus.SCHEDULED);

        when(assignmentDao.findByPatientId(10L)).thenReturn(List.of(completed, pending));
        when(resultDao.findByPatientId(10L)).thenReturn(Collections.emptyList());
        when(feedbackDao.findByPatientId(10L)).thenReturn(Collections.emptyList());

        PatientProgressDto progress = analyticsService.calculatePatientProgress(patient);

        assertEquals(2, progress.getTotalAssigned());
        assertEquals(1, progress.getTotalCompleted());
        assertEquals(50.0, progress.getCompletionRate());
    }
}
