package com.vrtherapy.util;

import com.vrtherapy.dto.PatientProgressDto;
import com.vrtherapy.model.SessionResult;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Report generation engine for clinical export in CSV and formatted text layouts.
 * Demonstrates Core Java string manipulation, I/O streams, and report formatting.
 */
public final class ReportGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private ReportGenerator() {
    }

    /**
     * Generates a comma-separated values (CSV) export of patient progress data.
     */
    public static byte[] generateCsvReport(PatientProgressDto progress) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8);

        // Header
        writer.println("Completed Date,Session ID,Duration (min),Pre-Stress (1-10),Post-Stress (1-10),Stress Delta,Improvement %,Avg HR (bpm),Peak HR (bpm),Notes");

        List<SessionResult> results = progress.getResults();
        if (results != null) {
            for (SessionResult result : results) {
                String date = result.getCompletedAt() != null ? result.getCompletedAt().format(DATE_FORMATTER) : "N/A";
                long sessionId = result.getSessionId() != null ? result.getSessionId() : 0L;
                int durationMin = result.getDurationSeconds() / 60;
                int preStress = result.getPreStressScore();
                int postStress = result.getPostStressScore();
                int delta = preStress - postStress;
                double pct = result.getImprovementPercentage();
                int avgHr = result.getAverageHeartRate();
                int peakHr = result.getPeakHeartRate();
                String notes = escapeCsv(result.getCompletionNotes());

                writer.printf("%s,Session #%d,%d,%d,%d,%d,%.1f%%,%d,%d,%s%n",
                        date, sessionId, durationMin, preStress, postStress, delta, pct, avgHr, peakHr, notes);
            }
        }
        writer.flush();
        return out.toByteArray();
    }

    /**
     * Generates a formal clinical ASCII/Text progress report.
     */
    public static byte[] generateTextReport(PatientProgressDto progress) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8);

        writer.println("================================================================================");
        writer.println("               VIRTUAL REALITY THERAPY REHABILITATION REPORT                    ");
        writer.println("================================================================================");
        writer.printf(" Patient Name       : %s%n", progress.getPatient().getFullName());
        writer.printf(" Patient Username   : %s%n", progress.getPatient().getUsername());
        writer.printf(" Patient Email      : %s%n", progress.getPatient().getEmail());
        writer.printf(" Report Generated   : %s%n", java.time.LocalDateTime.now().format(DATE_FORMATTER));
        writer.println("--------------------------------------------------------------------------------");
        writer.println(" CLINICAL PERFORMANCE SUMMARY                                                   ");
        writer.println("--------------------------------------------------------------------------------");
        writer.printf(" Total Assigned Sessions  : %d%n", progress.getTotalAssigned());
        writer.printf(" Completed Sessions       : %d%n", progress.getTotalCompleted());
        writer.printf(" Protocol Adherence Rate  : %.1f%%%n", progress.getCompletionRate());
        writer.printf(" Baseline Stress (Pre)    : %.1f / 10%n", progress.getAveragePreStress());
        writer.printf(" Post-Session Stress      : %.1f / 10%n", progress.getAveragePostStress());
        writer.printf(" Average Stress Delta     : %.1f points reduction%n", progress.getAverageStressDelta());
        writer.printf(" Overall Improvement Rate : %.1f%%%n", progress.getImprovementPercentage());
        writer.printf(" Therapeutic Trend        : %s%n", progress.getOverallTrend());
        writer.printf(" Average Patient Rating   : %.1f / 5.0%n", progress.getAverageRating());
        writer.printf(" Average Heart Rate       : %.1f bpm%n", progress.getAverageHeartRate());
        writer.println("--------------------------------------------------------------------------------");
        writer.println(" CHRONOLOGICAL SESSION LOG                                                      ");
        writer.println("--------------------------------------------------------------------------------");

        List<SessionResult> results = progress.getResults();
        if (results == null || results.isEmpty()) {
            writer.println(" No completed sessions recorded to date.");
        } else {
            for (int i = 0; i < results.size(); i++) {
                SessionResult res = results.get(i);
                writer.printf(" [%d] Date: %s | Session #%d%n",
                        i + 1,
                        res.getCompletedAt() != null ? res.getCompletedAt().format(DATE_FORMATTER) : "N/A",
                        res.getSessionId());
                writer.printf("     Duration: %d min | Pre-Stress: %d | Post-Stress: %d (Delta: %d)%n",
                        res.getDurationSeconds() / 60,
                        res.getPreStressScore(),
                        res.getPostStressScore(),
                        res.getPreStressScore() - res.getPostStressScore());
                writer.printf("     Vitals: Avg HR=%d bpm, Peak HR=%d bpm%n",
                        res.getAverageHeartRate(),
                        res.getPeakHeartRate());
                if (res.getCompletionNotes() != null && !res.getCompletionNotes().isEmpty()) {
                    writer.printf("     Clinical Notes: %s%n", res.getCompletionNotes());
                }
                writer.println();
            }
        }

        writer.println("================================================================================");
        writer.println(" End of Official Clinical Progress Document - VR Mental Health Platform         ");
        writer.println("================================================================================");

        writer.flush();
        return out.toByteArray();
    }

    private static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
