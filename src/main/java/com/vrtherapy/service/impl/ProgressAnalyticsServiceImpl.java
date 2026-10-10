package com.vrtherapy.service.impl;

import com.vrtherapy.dao.FeedbackDao;
import com.vrtherapy.dao.SessionAssignmentDao;
import com.vrtherapy.dao.SessionResultDao;
import com.vrtherapy.dao.impl.FeedbackDaoImpl;
import com.vrtherapy.dao.impl.SessionAssignmentDaoImpl;
import com.vrtherapy.dao.impl.SessionResultDaoImpl;
import com.vrtherapy.dto.PatientProgressDto;
import com.vrtherapy.model.AssignmentStatus;
import com.vrtherapy.model.Feedback;
import com.vrtherapy.model.SessionAssignment;
import com.vrtherapy.model.SessionResult;
import com.vrtherapy.model.User;
import com.vrtherapy.service.ProgressAnalyticsService;
import com.vrtherapy.util.ReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of ProgressAnalyticsService calculating all clinical metrics,
 * statistics, chart payloads, and report downloads entirely in Java.
 *
 * Demonstrates:
 * - Mathematical and statistical aggregation with Java Streams
 * - Formatted chart datasets for Chart.js
 * - Report rendering in CSV and text
 */
public class ProgressAnalyticsServiceImpl implements ProgressAnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(ProgressAnalyticsServiceImpl.class);
    private static final DateTimeFormatter CHART_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd");

    private final SessionAssignmentDao assignmentDao;
    private final SessionResultDao resultDao;
    private final FeedbackDao feedbackDao;

    public ProgressAnalyticsServiceImpl() {
        this(new SessionAssignmentDaoImpl(), new SessionResultDaoImpl(), new FeedbackDaoImpl());
    }

    public ProgressAnalyticsServiceImpl(SessionAssignmentDao assignmentDao,
                                        SessionResultDao resultDao,
                                        FeedbackDao feedbackDao) {
        this.assignmentDao = assignmentDao;
        this.resultDao = resultDao;
        this.feedbackDao = feedbackDao;
    }

    @Override
    public PatientProgressDto calculatePatientProgress(User patient) {
        logger.debug("Calculating clinical progress analytics for patient: {}", patient.getUsername());

        List<SessionAssignment> assignments = assignmentDao.findByPatientId(patient.getId());
        List<SessionResult> results = resultDao.findByPatientId(patient.getId());
        List<Feedback> feedbacks = feedbackDao.findByPatientId(patient.getId());

        PatientProgressDto dto = new PatientProgressDto();
        dto.setPatient(patient);
        dto.setAssignments(assignments);
        dto.setResults(results);
        dto.setFeedbacks(feedbacks);

        long totalAssigned = assignments.size();
        long totalCompleted = assignments.stream()
                .filter(a -> a.getStatus() == AssignmentStatus.COMPLETED)
                .count();

        dto.setTotalAssigned(totalAssigned);
        dto.setTotalCompleted(totalCompleted);

        double completionRate = totalAssigned > 0
                ? ((double) totalCompleted / totalAssigned) * 100.0
                : 0.0;
        dto.setCompletionRate(Math.round(completionRate * 10.0) / 10.0);

        if (results.isEmpty()) {
            dto.setAveragePreStress(0.0);
            dto.setAveragePostStress(0.0);
            dto.setAverageStressDelta(0.0);
            dto.setImprovementPercentage(0.0);
            dto.setOverallTrend("Baseline Data Gathering");
            dto.setAverageRating(0.0);
            dto.setAverageHeartRate(0.0);
            dto.setChartLabels(List.of());
            dto.setPreStressPoints(List.of());
            dto.setPostStressPoints(List.of());
            dto.setHeartRatePoints(List.of());
            return dto;
        }

        double avgPre = results.stream()
                .mapToInt(SessionResult::getPreStressScore)
                .average().orElse(0.0);

        double avgPost = results.stream()
                .mapToInt(SessionResult::getPostStressScore)
                .average().orElse(0.0);

        double delta = avgPre - avgPost;
        double improvementPct = avgPre > 0 ? (delta / avgPre) * 100.0 : 0.0;

        dto.setAveragePreStress(Math.round(avgPre * 10.0) / 10.0);
        dto.setAveragePostStress(Math.round(avgPost * 10.0) / 10.0);
        dto.setAverageStressDelta(Math.round(delta * 10.0) / 10.0);
        dto.setImprovementPercentage(Math.round(improvementPct * 10.0) / 10.0);

        // Clinical trend categorizer
        String trend;
        if (delta >= 2.0) {
            trend = "Marked Relaxation / High Response";
        } else if (delta >= 0.5) {
            trend = "Steady Progressive Desensitization";
        } else if (delta >= -0.5) {
            trend = "Autonomic Plateau / Stable Response";
        } else {
            trend = "Elevated Reactivity / Adjustment Needed";
        }
        dto.setOverallTrend(trend);

        double avgRating = feedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average().orElse(0.0);
        dto.setAverageRating(Math.round(avgRating * 10.0) / 10.0);

        double avgHr = results.stream()
                .mapToInt(SessionResult::getAverageHeartRate)
                .average().orElse(0.0);
        dto.setAverageHeartRate(Math.round(avgHr * 10.0) / 10.0);

        // Chart.js data series
        List<String> labels = new ArrayList<>();
        List<Integer> preScores = new ArrayList<>();
        List<Integer> postScores = new ArrayList<>();
        List<Integer> hrPoints = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            SessionResult res = results.get(i);
            String label = res.getCompletedAt() != null
                    ? res.getCompletedAt().format(CHART_DATE_FORMAT)
                    : "S-" + (i + 1);
            labels.add(label);
            preScores.add(res.getPreStressScore());
            postScores.add(res.getPostStressScore());
            hrPoints.add(res.getAverageHeartRate());
        }

        dto.setChartLabels(labels);
        dto.setPreStressPoints(preScores);
        dto.setPostStressPoints(postScores);
        dto.setHeartRatePoints(hrPoints);

        return dto;
    }

    @Override
    public byte[] generatePatientProgressCsv(User patient) {
        PatientProgressDto progress = calculatePatientProgress(patient);
        return ReportGenerator.generateCsvReport(progress);
    }

    @Override
    public byte[] generatePatientProgressTextReport(User patient) {
        PatientProgressDto progress = calculatePatientProgress(patient);
        return ReportGenerator.generateTextReport(progress);
    }
}
