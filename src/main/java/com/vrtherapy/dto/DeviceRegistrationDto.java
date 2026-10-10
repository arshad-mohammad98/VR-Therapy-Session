package com.vrtherapy.dto;

import com.vrtherapy.model.DeviceType;

public class DeviceRegistrationDto {

    private String deviceName;

    private DeviceType deviceType = DeviceType.HEADSET;

    private boolean webxrSupported = true;
    private boolean audioSupported = true;

    public DeviceRegistrationDto() {
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

    public boolean isWebxrSupported() {
        return webxrSupported;
    }

    public void setWebxrSupported(boolean webxrSupported) {
        this.webxrSupported = webxrSupported;
    }

    public boolean isAudioSupported() {
        return audioSupported;
    }

    public void setAudioSupported(boolean audioSupported) {
        this.audioSupported = audioSupported;
    }
}
