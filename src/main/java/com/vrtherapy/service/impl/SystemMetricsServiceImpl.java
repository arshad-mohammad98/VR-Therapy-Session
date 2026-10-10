package com.vrtherapy.service.impl;

import com.vrtherapy.dao.DeviceDao;
import com.vrtherapy.dao.SessionDao;
import com.vrtherapy.dao.UserDao;
import com.vrtherapy.dao.impl.DeviceDaoImpl;
import com.vrtherapy.dao.impl.SessionDaoImpl;
import com.vrtherapy.dao.impl.UserDaoImpl;
import com.vrtherapy.dto.SystemStatusDto;
import com.vrtherapy.listener.AppSessionListener;
import com.vrtherapy.service.SystemMetricsService;
import com.vrtherapy.thread.VrConnectionRegistry;

import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service implementation collecting JVM memory metrics, platform uptime,
 * servlet response timings, and device counts.
 */
public class SystemMetricsServiceImpl implements SystemMetricsService {

    private static final SystemMetricsServiceImpl INSTANCE = new SystemMetricsServiceImpl();

    private final UserDao userDao;
    private final SessionDao sessionDao;
    private final DeviceDao deviceDao;

    private final AtomicLong totalRequestDurationMs = new AtomicLong(0);
    private final AtomicLong totalRequestsHandled = new AtomicLong(0);

    public SystemMetricsServiceImpl() {
        this(new UserDaoImpl(), new SessionDaoImpl(), new DeviceDaoImpl());
    }

    public SystemMetricsServiceImpl(UserDao userDao, SessionDao sessionDao, DeviceDao deviceDao) {
        this.userDao = userDao;
        this.sessionDao = sessionDao;
        this.deviceDao = deviceDao;
    }

    public static SystemMetricsServiceImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public void recordRequestMetric(String endpoint, long durationMs) {
        totalRequestDurationMs.addAndGet(durationMs);
        totalRequestsHandled.incrementAndGet();
    }

    @Override
    public SystemStatusDto getSystemStatus() {
        SystemStatusDto dto = new SystemStatusDto();

        // JVM runtime metrics
        Runtime runtime = Runtime.getRuntime();
        long totalMem = runtime.totalMemory() / (1024 * 1024);
        long freeMem = runtime.freeMemory() / (1024 * 1024);
        long usedMem = totalMem - freeMem;
        long maxMem = runtime.maxMemory() / (1024 * 1024);

        dto.setUsedMemoryMb(usedMem);
        dto.setTotalMemoryMb(totalMem);
        dto.setMaxMemoryMb(maxMem);

        // Uptime from JVM RuntimeMXBean
        long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
        dto.setUptimeFormatted(formatUptime(uptimeMs));

        // Average response time
        long count = totalRequestsHandled.get();
        double avgResponseTime = count > 0 ? (double) totalRequestDurationMs.get() / count : 12.0;
        dto.setAvgResponseTimeMs(Math.round(avgResponseTime * 10.0) / 10.0);

        // Platform entity counters
        dto.setTotalUsers(userDao.count());
        dto.setTotalSessions(sessionDao.count());
        dto.setActiveWebSessions((long) AppSessionListener.getActiveSessionsCount());
        dto.setActiveLiveSessions((long) VrConnectionRegistry.getInstance().getActiveLiveSessionsCount());
        dto.setConnectedDevices((long) VrConnectionRegistry.getInstance().getConnectedDevicesCount());

        return dto;
    }

    private String formatUptime(long uptimeMs) {
        long seconds = uptimeMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return String.format("%d days, %d hrs", days, hours % 24);
        } else if (hours > 0) {
            return String.format("%d hrs, %d mins", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d mins, %d secs", minutes, seconds % 60);
        } else {
            return String.format("%d seconds", seconds);
        }
    }
}
