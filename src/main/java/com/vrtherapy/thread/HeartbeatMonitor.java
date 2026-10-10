package com.vrtherapy.thread;

import com.vrtherapy.dao.DeviceDao;
import com.vrtherapy.dao.impl.DeviceDaoImpl;
import com.vrtherapy.model.DeviceStatus;
import com.vrtherapy.model.VrDevice;
import com.vrtherapy.observer.AlertManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Background daemon thread scheduled via ScheduledExecutorService.
 * Scans active devices and marks those without recent heartbeats as DISCONNECTED.
 *
 * Demonstrates:
 * - Multithreading (Scheduled thread execution)
 * - Safe error handling without crashing background loop
 * - Observer pattern integration (AlertManager notifications)
 */
public class HeartbeatMonitor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatMonitor.class);
    private final DeviceDao deviceDao;
    private final int timeoutSeconds;

    public HeartbeatMonitor(int timeoutSeconds) {
        this(new DeviceDaoImpl(), timeoutSeconds);
    }

    public HeartbeatMonitor(DeviceDao deviceDao, int timeoutSeconds) {
        this.deviceDao = deviceDao;
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public void run() {
        try {
            LocalDateTime threshold = LocalDateTime.now().minusSeconds(timeoutSeconds);
            List<VrDevice> timedOut = deviceDao.findTimedOutDevices(threshold);
            for (VrDevice dev : timedOut) {
                logger.warn("Device [{}] (ID={}) heartbeat timed out. Flagging DISCONNECTED.",
                        dev.getDeviceName(), dev.getId());
                deviceDao.updateStatus(dev.getId(), DeviceStatus.DISCONNECTED);

                if (dev.getPairingCode() != null) {
                    VrConnectionRegistry.getInstance().unregisterDevice(dev.getPairingCode());
                }

                // Notify observers of device disconnect
                AlertManager.getInstance().broadcastAlert(
                        "DEVICE_DISCONNECTED",
                        "Device " + dev.getDeviceName() + " lost connection during active monitoring.",
                        dev.getPatientId()
                );
            }
        } catch (Exception e) {
            logger.error("Error in HeartbeatMonitor execution cycle: {}", e.getMessage(), e);
        }
    }
}
