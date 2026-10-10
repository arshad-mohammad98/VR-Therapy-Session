package com.vrtherapy.util;

import com.vrtherapy.dto.PatientProgressDto;
import com.vrtherapy.model.Patient;
import com.vrtherapy.model.SessionResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportGeneratorTest {

    @Test
    @DisplayName("CSV export should generate correct headers and patient session columns")
    void testGenerateCsvReportContainsHeadersAndData() {
        Patient patient = new Patient("patient1", "pass", "Sarah Connor", "sarah@example.com", true);
        SessionResult result = new SessionResult();
        result.setSessionId(101L);
        result.setPatientId(1L);
        result.setDurationSeconds(1200);
        result.setPreStressScore(8);
        result.setPostStressScore(4);
        result.setImprovementPercentage(50.0);
        result.setAverageHeartRate(75);
        result.setPeakHeartRate(88);
        result.setCompletedAt(LocalDateTime.now());
        result.setCompletionNotes("Marked improvement observed");

        PatientProgressDto progress = new PatientProgressDto();
        progress.setPatient(patient);
        progress.setResults(List.of(result));

        byte[] csvBytes = ReportGenerator.generateCsvReport(progress);
        String csvContent = new String(csvBytes, StandardCharsets.UTF_8);

        assertTrue(csvContent.contains("Completed Date,Session ID,Duration (min),Pre-Stress (1-10),Post-Stress (1-10)"));
        assertTrue(csvContent.contains("Session #101"));
        assertTrue(csvContent.contains("50.0%"));
        assertTrue(csvContent.contains("Marked improvement observed"));
    }

    @Test
    @DisplayName("Text report should produce formatted clinical rehabilitation document")
    void testGenerateTextReportContainsPatientAndSummary() {
        Patient patient = new Patient("patient1", "pass", "Sarah Connor", "sarah@example.com", true);
        PatientProgressDto progress = new PatientProgressDto();
        progress.setPatient(patient);
        progress.setTotalAssigned(5);
        progress.setTotalCompleted(4);
        progress.setCompletionRate(80.0);
        progress.setAveragePreStress(7.5);
        progress.setAveragePostStress(3.2);
        progress.setAverageStressDelta(4.3);
        progress.setImprovementPercentage(57.3);
        progress.setOverallTrend("Marked Relaxation");
        progress.setAverageRating(4.8);
        progress.setAverageHeartRate(72.0);
        progress.setResults(List.of());

        byte[] textBytes = ReportGenerator.generateTextReport(progress);
        String report = new String(textBytes, StandardCharsets.UTF_8);

        assertTrue(report.contains("VIRTUAL REALITY THERAPY REHABILITATION REPORT"));
        assertTrue(report.contains("Sarah Connor"));
        assertTrue(report.contains("Protocol Adherence Rate  : 80.0%"));
        assertTrue(report.contains("Baseline Stress (Pre)    : 7.5 / 10"));
        assertTrue(report.contains("Marked Relaxation"));
    }
}
