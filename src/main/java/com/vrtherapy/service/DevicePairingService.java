package com.vrtherapy.service;

import com.vrtherapy.model.DeviceType;
import com.vrtherapy.model.User;
import com.vrtherapy.model.VrDevice;
import java.util.List;
import java.util.Optional;

/**
 * Service orchestrating VR hardware registration, cryptographic 6-digit pairing code life cycles,
 * and device connectivity health verification.
 */
public interface DevicePairingService {

    VrDevice registerDevice(User patient, String deviceName, DeviceType deviceType, boolean webxr, boolean audio);

    VrDevice generateNewPairingCode(Long deviceId, User patient);

    VrDevice pairDeviceWithCode(String pairingCode, String clientIdentifier, String ipAddress);

    void recordHeartbeat(Long deviceId);

    void disconnectDevice(Long deviceId);

    void blockDevice(Long deviceId);

    List<VrDevice> getDevicesForPatient(User patient);

    List<VrDevice> getAllDevices();

    Optional<VrDevice> getDeviceById(Long deviceId);

    boolean verifyPairingCodeValidity(VrDevice device);
}
