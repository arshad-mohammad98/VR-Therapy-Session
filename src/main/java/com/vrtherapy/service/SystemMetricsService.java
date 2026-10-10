package com.vrtherapy.service;

import com.vrtherapy.dto.SystemStatusDto;

/**
 * Service contract collecting JVM runtime telemetry, request timings from interceptors,
 * and high-level platform status aggregates.
 */
public interface SystemMetricsService {

    /**
     * Records response time for an HTTP request intercepted by RequestMetricsInterceptor.
     */
    void recordRequestMetric(String endpoint, long durationMs);

    /**
     * Synthesizes and returns real-time system status indicators.
     */
    SystemStatusDto getSystemStatus();
}
