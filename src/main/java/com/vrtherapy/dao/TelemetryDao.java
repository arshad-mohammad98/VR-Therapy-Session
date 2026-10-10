package com.vrtherapy.dao;

import com.vrtherapy.model.TelemetrySample;

import java.util.List;

public interface TelemetryDao extends GenericDao<TelemetrySample, Long> {

    List<TelemetrySample> findRecentByLiveSessionId(Long liveSessionId, int limit);

    void batchInsert(List<TelemetrySample> samples);

    double calculateAverageHeartRate(Long liveSessionId);

    int findPeakHeartRate(Long liveSessionId);
}
