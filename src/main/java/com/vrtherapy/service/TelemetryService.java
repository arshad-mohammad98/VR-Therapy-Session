package com.vrtherapy.service;

import com.vrtherapy.model.LiveSession;
import com.vrtherapy.model.TelemetrySample;
import com.vrtherapy.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Service processing live physiological telemetry streams, persisting samples,
 * and evaluating clinical crisis detection rules in real-time.
 */
public interface TelemetryService {

    TelemetrySample processSample(Long liveSessionId, User patient, int heartRate, int stressLevel);

    List<TelemetrySample> getSamplesForSession(Long liveSessionId);

    List<TelemetrySample> getRecentSamplesForPatient(User patient);

    Optional<String> checkCrisisStatus(LiveSession session, int heartRate, int stressLevel);
}
