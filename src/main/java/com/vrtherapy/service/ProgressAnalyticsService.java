package com.vrtherapy.service;

import com.vrtherapy.dto.PatientProgressDto;
import com.vrtherapy.model.User;

/**
 * Service contract calculating clinical biometric analytics, stress trends,
 * completion rates, and formatting data structures for Chart.js.
 */
public interface ProgressAnalyticsService {

    /**
     * Calculates comprehensive progress and trend analytics for a given patient.
     */
    PatientProgressDto calculatePatientProgress(User patient);

    /**
     * Generates a downloadable CSV report for the specified patient.
     */
    byte[] generatePatientProgressCsv(User patient);

    /**
     * Generates a downloadable clinical text report for the specified patient.
     */
    byte[] generatePatientProgressTextReport(User patient);
}
