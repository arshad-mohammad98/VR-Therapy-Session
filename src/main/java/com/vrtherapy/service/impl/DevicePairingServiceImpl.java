package com.vrtherapy.service.impl;

import com.vrtherapy.dao.DeviceDao;
import com.vrtherapy.dao.impl.DeviceDaoImpl;
import com.vrtherapy.exception.DeviceNotFoundException;
import com.vrtherapy.exception.ResourceNotFoundException;
import com.vrtherapy.exception.ValidationException;
import com.vrtherapy.model.DeviceStatus;
import com.vrtherapy.model.DeviceType;
import com.vrtherapy.model.User;
import com.vrtherapy.model.VrDevice;
import com.vrtherapy.service.DevicePairingService;
import com.vrtherapy.thread.VrConnectionRegistry;
import com.vrtherapy.util.PairingCodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for VR headset and browser device pairing,
 * 6-digit expiring pairing code generation, and heartbeat monitoring.
 */
public class DevicePairingServiceImpl implements DevicePairingService {

    private static final Logger logger = LoggerFactory.getLogger(DevicePairingServiceImpl.class);
    private static final int CODE_EXPIRY_MINUTES = 5;

    private final DeviceDao deviceDao;

    public DevicePairingServiceImpl() {
        this(new DeviceDaoImpl());
    }

    public DevicePairingServiceImpl(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    @Override
    public VrDevice registerDevice(User patient, String deviceName, DeviceType deviceType, boolean webxr, boolean audio) {
        VrDevice device = new VrDevice(deviceName, deviceType, patient != null ? patient.getId() : null);
        String code = PairingCodeGenerator.generateSixDigitCode();
        device.setPairingCode(code);
        device.setCodeExpiresAt(LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));
        device.setStatus(DeviceStatus.PAIRING);

        VrDevice saved = deviceDao.save(device);
        logger.info("Registered VR device '{}' with pairing code {} (expires in 5 mins)", deviceName, code);
        return saved;
    }

    @Override
    public VrDevice generateNewPairingCode(Long deviceId, User patient) {
        VrDevice device = getDeviceById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found: " + deviceId));

        String newCode = PairingCodeGenerator.generateSixDigitCode();
        device.setPairingCode(newCode);
        device.setCodeExpiresAt(LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));
        device.setStatus(DeviceStatus.PAIRING);

        deviceDao.update(device);
        logger.info("Generated fresh pairing code {} for device {}", newCode, deviceId);
        return device;
    }

    @Override
    public VrDevice pairDeviceWithCode(String pairingCode, String clientIdentifier, String ipAddress) {
        if (pairingCode == null || pairingCode.trim().length() != 6) {
            throw new ValidationException("Pairing code must be a 6-digit numeric sequence.");
        }

        VrDevice device = deviceDao.findByPairingCode(pairingCode.trim())
                .orElseThrow(() -> new DeviceNotFoundException("No active device found with pairing code: " + pairingCode));

        if (device.isBlocked()) {
            throw new ValidationException("This device has been administratively blocked.");
        }

        if (device.isCodeExpired()) {
            device.setStatus(DeviceStatus.DISCONNECTED);
            deviceDao.updateStatus(device.getId(), DeviceStatus.DISCONNECTED);
            throw new ValidationException("Pairing code has expired. Please generate a new code in your dashboard.");
        }

        device.setStatus(DeviceStatus.CONNECTED);
        device.setLastHeartbeat(LocalDateTime.now());
        device.setIpAddress(ipAddress);
        deviceDao.update(device);

        // Register in active in-memory registry
        VrConnectionRegistry.getInstance().registerDevice(
                pairingCode,
                device.getId(),
                device.getDeviceName(),
                ipAddress
        );

        logger.info("Device '{}' (ID={}) successfully connected via code {}", device.getDeviceName(), device.getId(), pairingCode);
        return device;
    }

    @Override
    public void recordHeartbeat(Long deviceId) {
        deviceDao.updateHeartbeat(deviceId, LocalDateTime.now());
    }

    @Override
    public void disconnectDevice(Long deviceId) {
        Optional<VrDevice> opt = deviceDao.findById(deviceId);
        if (opt.isPresent()) {
            VrDevice dev = opt.get();
            deviceDao.updateStatus(deviceId, DeviceStatus.DISCONNECTED);
            if (dev.getPairingCode() != null) {
                VrConnectionRegistry.getInstance().unregisterDevice(dev.getPairingCode());
            }
            logger.info("Device #{} disconnected.", deviceId);
        }
    }

    @Override
    public void blockDevice(Long deviceId) {
        deviceDao.setBlocked(deviceId, true);
        logger.warn("Device #{} was administratively blocked.", deviceId);
    }

    @Override
    public List<VrDevice> getDevicesForPatient(User patient) {
        if (patient == null) {
            return List.of();
        }
        return deviceDao.findByPatientId(patient.getId());
    }

    @Override
    public List<VrDevice> getAllDevices() {
        return deviceDao.findAll();
    }

    @Override
    public Optional<VrDevice> getDeviceById(Long deviceId) {
        return deviceDao.findById(deviceId);
    }

    @Override
    public boolean verifyPairingCodeValidity(VrDevice device) {
        return device != null && device.getPairingCode() != null && !device.isCodeExpired();
    }
}
