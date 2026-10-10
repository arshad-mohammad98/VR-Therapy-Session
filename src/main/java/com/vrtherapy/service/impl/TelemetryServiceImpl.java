package com.vrtherapy.service.impl;

import com.vrtherapy.dao.LiveSessionDao;
import com.vrtherapy.dao.TelemetryDao;
import com.vrtherapy.dao.impl.LiveSessionDaoImpl;
import com.vrtherapy.dao.impl.TelemetryDaoImpl;
import com.vrtherapy.model.LiveSession;
import com.vrtherapy.model.TelemetrySample;
import com.vrtherapy.model.User;
import com.vrtherapy.observer.AlertManager;
import com.vrtherapy.service.TelemetryService;
import com.vrtherapy.strategy.CrisisContext;
import com.vrtherapy.strategy.CrisisEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class TelemetryServiceImpl implements TelemetryService {

    private static final Logger logger = LoggerFactory.getLogger(TelemetryServiceImpl.class);

    private final TelemetryDao telemetryDao;
    private final LiveSessionDao liveSessionDao;
    private final CrisisEngine crisisEngine;

    public TelemetryServiceImpl() {
        this(new TelemetryDaoImpl(), new LiveSessionDaoImpl(), new CrisisEngine());
    }

    public TelemetryServiceImpl(TelemetryDao telemetryDao, LiveSessionDao liveSessionDao, CrisisEngine crisisEngine) {
        this.telemetryDao = telemetryDao;
        this.liveSessionDao = liveSessionDao;
        this.crisisEngine = crisisEngine;
    }

    @Override
    public TelemetrySample processSample(Long liveSessionId, User patient, int heartRate, int stressLevel) {
        TelemetrySample sample = new TelemetrySample(
                liveSessionId,
                patient != null ? patient.getId() : null,
                heartRate,
                stressLevel,
                Math.max(40, 80 - (stressLevel * 4)),
                14 + (stressLevel > 6 ? 4 : 0)
        );
        TelemetrySample saved = telemetryDao.save(sample);

        // Update live session active state
        if (liveSessionId != null) {
            liveSessionDao.findById(liveSessionId).ifPresent(session -> {
                liveSessionDao.updateLiveParameters(liveSessionId, heartRate, stressLevel, session.getElapsedSeconds() + 2);

                // Check crisis rules via Strategy pattern
                Optional<String> crisisAlert = checkCrisisStatus(session, heartRate, stressLevel);
                crisisAlert.ifPresent(alert -> {
                    AlertManager.getInstance().broadcastAlert(
                            "CRITICAL_DISTRESS",
                            alert,
                            session.getPatientId()
                    );
                });
            });
        }

        return saved;
    }

    @Override
    public List<TelemetrySample> getSamplesForSession(Long liveSessionId) {
        return telemetryDao.findRecentByLiveSessionId(liveSessionId, 50);
    }

    @Override
    public List<TelemetrySample> getRecentSamplesForPatient(User patient) {
        if (patient == null) {
            return List.of();
        }
        return telemetryDao.findAll();
    }

    @Override
    public Optional<String> checkCrisisStatus(LiveSession session, int heartRate, int stressLevel) {
        CrisisContext ctx = new CrisisContext(
                session,
                heartRate,
                stressLevel,
                session.getElapsedSeconds(),
                heartRate - 75
        );
        return crisisEngine.evaluateCrisis(ctx);
    }
}
