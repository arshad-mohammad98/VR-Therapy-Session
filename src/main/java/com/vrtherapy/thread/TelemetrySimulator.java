package com.vrtherapy.thread;

import com.vrtherapy.dao.LiveSessionDao;
import com.vrtherapy.dao.TelemetryDao;
import com.vrtherapy.dao.impl.LiveSessionDaoImpl;
import com.vrtherapy.dao.impl.TelemetryDaoImpl;
import com.vrtherapy.model.LiveSession;
import com.vrtherapy.model.LiveSessionState;
import com.vrtherapy.model.TelemetrySample;
import com.vrtherapy.observer.AlertManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Runnable simulator generating realistic physiological biometrics
 * (heart rate, galvanic stress index, HRV, respiration) for an active live session.
 *
 * Demonstrates:
 * - Multithreading & Concurrency (Runnable task on thread pool)
 * - Thread interruption handling
 * - Real-time telemetry simulation with physiological dynamics
 */
public class TelemetrySimulator implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TelemetrySimulator.class);

    private final Long liveSessionId;
    private final Long patientId;
    private final LiveSessionDao liveSessionDao;
    private final TelemetryDao telemetryDao;
    private final Random random = new Random();

    private volatile boolean running = true;
    private int baselineHeartRate = 72;
    private int currentStress = 4;
    private int elapsedSeconds = 0;

    public TelemetrySimulator(Long liveSessionId, Long patientId) {
        this(liveSessionId, patientId, new LiveSessionDaoImpl(), new TelemetryDaoImpl());
    }

    public TelemetrySimulator(Long liveSessionId, Long patientId, LiveSessionDao liveSessionDao, TelemetryDao telemetryDao) {
        this.liveSessionId = liveSessionId;
        this.patientId = patientId;
        this.liveSessionDao = liveSessionDao;
        this.telemetryDao = telemetryDao;
    }

    public void stopSimulation() {
        this.running = false;
    }

    @Override
    public void run() {
        logger.info("Starting TelemetrySimulator thread for live session {} (Patient: {})", liveSessionId, patientId);
        try {
            while (running && !Thread.currentThread().isInterrupted()) {
                // Check if session is still ACTIVE or WAITING
                var optSession = liveSessionDao.findById(liveSessionId);
                if (optSession.isEmpty()) {
                    break;
                }
                LiveSession session = optSession.get();
                if (session.getState() == LiveSessionState.COMPLETED ||
                    session.getState() == LiveSessionState.EMERGENCY_STOPPED) {
                    logger.info("Session {} in terminal state {}. Stopping simulator thread.", liveSessionId, session.getState());
                    break;
                }

                if (session.getState() == LiveSessionState.ACTIVE) {
                    elapsedSeconds += 2;
                    int intensity = session.getCurrentIntensity();

                    // Physiological calculation based on intensity and breathing guide
                    int targetHeartRate = 68 + (intensity * 6);
                    if (session.isBreathingGuideActive()) {
                        targetHeartRate -= 5;
                    }
                    int noise = random.nextInt(7) - 3;
                    baselineHeartRate = Math.max(58, Math.min(130, baselineHeartRate + (targetHeartRate > baselineHeartRate ? 1 : -1) + noise));

                    currentStress = Math.max(1, Math.min(10, intensity + (baselineHeartRate > 90 ? 3 : 1) + (random.nextInt(3) - 1)));
                    int hrv = Math.max(35, 80 - (currentStress * 4) + random.nextInt(6));
                    int breathingRate = session.isBreathingGuideActive() ? 10 + random.nextInt(3) : 14 + (intensity) + random.nextInt(3);

                    // Update live session parameters in DB
                    liveSessionDao.updateLiveParameters(liveSessionId, baselineHeartRate, currentStress, elapsedSeconds);

                    // Insert telemetry sample
                    TelemetrySample sample = new TelemetrySample(liveSessionId, patientId, baselineHeartRate, currentStress, hrv, breathingRate);
                    telemetryDao.save(sample);

                    // Crisis threshold detection
                    if (baselineHeartRate >= 120 || currentStress >= 9) {
                        AlertManager.getInstance().broadcastAlert(
                                "CRITICAL_VITALS",
                                "Acute distress detected: HR=" + baselineHeartRate + " bpm, Stress=" + currentStress + "/10",
                                patientId
                        );
                    }
                }

                // Sleep for 2-second simulation interval
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.info("TelemetrySimulator thread interrupted for session {}", liveSessionId);
        } catch (Exception e) {
            logger.error("Error in TelemetrySimulator thread: {}", e.getMessage(), e);
        } finally {
            logger.info("TelemetrySimulator thread concluded for session {}", liveSessionId);
        }
    }
}
