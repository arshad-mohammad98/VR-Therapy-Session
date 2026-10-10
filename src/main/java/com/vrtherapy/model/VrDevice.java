package com.vrtherapy.model;

import java.time.LocalDateTime;

/**
 * Domain entity representing hardware VR headsets, browser WebXR sessions, or mobile VR mounts.
 */
public class VrDevice extends BaseEntity {

    private String deviceName;
    private DeviceType deviceType;
    private String pairingCode;
    private LocalDateTime codeExpiresAt;
    private DeviceStatus status = DeviceStatus.DISCONNECTED;
    private LocalDateTime lastHeartbeat;
    private Long patientId;
    private String ipAddress;
    private boolean blocked = false;

    public VrDevice() {
        super();
    }

    public VrDevice(String deviceName, DeviceType deviceType, Long patientId) {
        super();
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.patientId = patientId;
    }

    public boolean isCodeExpired() {
        return codeExpiresAt != null && LocalDateTime.now().isAfter(codeExpiresAt);
    }

    public boolean isPairingCodeValid() {
        return !isCodeExpired() && pairingCode != null;
    }

    public LocalDateTime getPairingCodeExpiresAt() {
        return codeExpiresAt;
    }

    public void setPairingCodeExpiresAt(LocalDateTime expiresAt) {
        this.codeExpiresAt = expiresAt;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getPairingCode() {
        return pairingCode;
    }

    public void setPairingCode(String pairingCode) {
        this.pairingCode = pairingCode;
    }

    public LocalDateTime getCodeExpiresAt() {
        return codeExpiresAt;
    }

    public void setCodeExpiresAt(LocalDateTime codeExpiresAt) {
        this.codeExpiresAt = codeExpiresAt;
    }

    public DeviceStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceStatus status) {
        this.status = status;
    }

    public LocalDateTime getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(LocalDateTime lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
