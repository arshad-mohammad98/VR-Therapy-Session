package com.vrtherapy.dto;

import java.util.Map;

/**
 * Analytical DTO packaging live server telemetry, resource utilization, and health metrics.
 */
public class SystemStatusDto {

    private String applicationName;
    private String uptimeFormatted;
    private long uptimeSeconds;
    private long totalUsers;
    private long activeUsers;
    private long totalSessions;
    private long activeSessions;
    private long totalAssignments;
    private long completedAssignments;
    private long openTickets;
    private long connectedDevices;
    private long activeLiveSessions;

    // JVM Telemetry
    private long heapMemoryUsedMb;
    private long heapMemoryMaxMb;
    private double heapMemoryUsagePercent;
    private int availableProcessors;

    // Interceptor Metrics
    private long totalRequestsServed;
    private double averageResponseTimeMs;
    private Map<String, Long> requestsByEndpoint;

    public SystemStatusDto() {
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getUptimeFormatted() {
        return uptimeFormatted;
    }

    public void setUptimeFormatted(String uptimeFormatted) {
        this.uptimeFormatted = uptimeFormatted;
    }

    public long getUptimeSeconds() {
        return uptimeSeconds;
    }

    public void setUptimeSeconds(long uptimeSeconds) {
        this.uptimeSeconds = uptimeSeconds;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public long getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(long totalSessions) {
        this.totalSessions = totalSessions;
    }

    public long getActiveSessions() {
        return activeSessions;
    }

    public void setActiveSessions(long activeSessions) {
        this.activeSessions = activeSessions;
    }

    public long getTotalAssignments() {
        return totalAssignments;
    }

    public void setTotalAssignments(long totalAssignments) {
        this.totalAssignments = totalAssignments;
    }

    public long getCompletedAssignments() {
        return completedAssignments;
    }

    public void setCompletedAssignments(long completedAssignments) {
        this.completedAssignments = completedAssignments;
    }

    public long getOpenTickets() {
        return openTickets;
    }

    public void setOpenTickets(long openTickets) {
        this.openTickets = openTickets;
    }

    public long getHeapMemoryUsedMb() {
        return heapMemoryUsedMb;
    }

    public void setHeapMemoryUsedMb(long heapMemoryUsedMb) {
        this.heapMemoryUsedMb = heapMemoryUsedMb;
    }

    public long getHeapMemoryMaxMb() {
        return heapMemoryMaxMb;
    }

    public void setHeapMemoryMaxMb(long heapMemoryMaxMb) {
        this.heapMemoryMaxMb = heapMemoryMaxMb;
    }

    public double getHeapMemoryUsagePercent() {
        return heapMemoryUsagePercent;
    }

    public void setHeapMemoryUsagePercent(double heapMemoryUsagePercent) {
        this.heapMemoryUsagePercent = heapMemoryUsagePercent;
    }

    public int getAvailableProcessors() {
        return availableProcessors;
    }

    public void setAvailableProcessors(int availableProcessors) {
        this.availableProcessors = availableProcessors;
    }

    public long getTotalRequestsServed() {
        return totalRequestsServed;
    }

    public void setTotalRequestsServed(long totalRequestsServed) {
        this.totalRequestsServed = totalRequestsServed;
    }

    public double getAverageResponseTimeMs() {
        return averageResponseTimeMs;
    }

    public void setAverageResponseTimeMs(double averageResponseTimeMs) {
        this.averageResponseTimeMs = averageResponseTimeMs;
    }

    public Map<String, Long> getRequestsByEndpoint() {
        return requestsByEndpoint;
    }

    public void setRequestsByEndpoint(Map<String, Long> requestsByEndpoint) {
        this.requestsByEndpoint = requestsByEndpoint;
    }

    public long getConnectedDevices() {
        return connectedDevices;
    }

    public void setConnectedDevices(long connectedDevices) {
        this.connectedDevices = connectedDevices;
    }

    public long getActiveLiveSessions() {
        return activeLiveSessions;
    }

    public void setActiveLiveSessions(long activeLiveSessions) {
        this.activeLiveSessions = activeLiveSessions;
    }
}
