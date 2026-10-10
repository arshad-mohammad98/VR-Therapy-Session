package com.vrtherapy.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory thread-safe registry tracking active VR headsets, WebXR browser sessions,
 * and concurrent live sessions.
 *
 * Demonstrates:
 * - Multithreading & Synchronization (ConcurrentHashMap, AtomicInteger, synchronized methods)
 */
public class VrConnectionRegistry {

    private static final Logger logger = LoggerFactory.getLogger(VrConnectionRegistry.class);
    private static final VrConnectionRegistry INSTANCE = new VrConnectionRegistry();

    private final Map<String, DeviceConnectionInfo> activeDevices = new ConcurrentHashMap<>();
    private final Map<Long, Long> sessionDeviceMap = new ConcurrentHashMap<>(); // LiveSessionId -> DeviceId
    private final AtomicInteger activeLiveSessionsCounter = new AtomicInteger(0);

    private VrConnectionRegistry() {
    }

    public static VrConnectionRegistry getInstance() {
        return INSTANCE;
    }

    public static class DeviceConnectionInfo {
        private final Long deviceId;
        private final String deviceName;
        private final String ipAddress;
        private volatile LocalDateTime lastPing;
        private volatile long latencyMs;

        public DeviceConnectionInfo(Long deviceId, String deviceName, String ipAddress) {
            this.deviceId = deviceId;
            this.deviceName = deviceName;
            this.ipAddress = ipAddress;
            this.lastPing = LocalDateTime.now();
            this.latencyMs = 12L;
        }

        public Long getDeviceId() {
            return deviceId;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public LocalDateTime getLastPing() {
            return lastPing;
        }

        public void setLastPing(LocalDateTime lastPing) {
            this.lastPing = lastPing;
        }

        public long getLatencyMs() {
            return latencyMs;
        }

        public void setLatencyMs(long latencyMs) {
            this.latencyMs = latencyMs;
        }
    }

    public void registerDevice(String pairingCode, Long deviceId, String deviceName, String ipAddress) {
        DeviceConnectionInfo info = new DeviceConnectionInfo(deviceId, deviceName, ipAddress);
        activeDevices.put(pairingCode, info);
        logger.info("Device [{}] paired and registered with code {}", deviceName, pairingCode);
    }

    public void recordHeartbeat(String pairingCode, long latencyMs) {
        DeviceConnectionInfo info = activeDevices.get(pairingCode);
        if (info != null) {
            info.setLastPing(LocalDateTime.now());
            info.setLatencyMs(latencyMs);
        }
    }

    public void unregisterDevice(String pairingCode) {
        DeviceConnectionInfo removed = activeDevices.remove(pairingCode);
        if (removed != null) {
            logger.info("Device [{}] unregistered from active registry.", removed.getDeviceName());
        }
    }

    public boolean isDeviceActive(String pairingCode) {
        return activeDevices.containsKey(pairingCode);
    }

    public DeviceConnectionInfo getDeviceInfo(String pairingCode) {
        return activeDevices.get(pairingCode);
    }

    public int getConnectedDevicesCount() {
        return activeDevices.size();
    }

    public void incrementActiveSessions() {
        int count = activeLiveSessionsCounter.incrementAndGet();
        logger.info("Active live sessions count incremented to {}", count);
    }

    public void decrementActiveSessions() {
        int count = activeLiveSessionsCounter.decrementAndGet();
        if (count < 0) {
            activeLiveSessionsCounter.set(0);
        }
        logger.info("Active live sessions count decremented to {}", activeLiveSessionsCounter.get());
    }

    public int getActiveLiveSessionsCount() {
        return activeLiveSessionsCounter.get();
    }

    public void associateSessionWithDevice(Long liveSessionId, Long deviceId) {
        sessionDeviceMap.put(liveSessionId, deviceId);
    }

    public Long getDeviceForSession(Long liveSessionId) {
        return sessionDeviceMap.get(liveSessionId);
    }

    public void removeSessionDeviceAssociation(Long liveSessionId) {
        sessionDeviceMap.remove(liveSessionId);
    }
}
