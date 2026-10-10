package com.vrtherapy.dao;

import com.vrtherapy.model.DeviceStatus;
import com.vrtherapy.model.VrDevice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DeviceDao extends GenericDao<VrDevice, Long> {

    Optional<VrDevice> findByPairingCode(String pairingCode);

    List<VrDevice> findByPatientId(Long patientId);

    void updateStatus(Long deviceId, DeviceStatus status);

    void updateHeartbeat(Long deviceId, LocalDateTime heartbeat);

    void setBlocked(Long deviceId, boolean blocked);

    List<VrDevice> findTimedOutDevices(LocalDateTime threshold);
}
