package com.vrtherapy.service;

import com.vrtherapy.dao.DeviceDao;
import com.vrtherapy.exception.ValidationException;
import com.vrtherapy.model.DeviceStatus;
import com.vrtherapy.model.DeviceType;
import com.vrtherapy.model.Patient;
import com.vrtherapy.model.VrDevice;
import com.vrtherapy.service.impl.DevicePairingServiceImpl;
import com.vrtherapy.thread.VrConnectionRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DevicePairingServiceTest {

    @Mock
    private DeviceDao deviceDao;

    private DevicePairingServiceImpl devicePairingService;
    private Patient samplePatient;

    @BeforeEach
    void setUp() {
        devicePairingService = new DevicePairingServiceImpl(deviceDao);

        samplePatient = new Patient("patient1", "pass", "Sarah Connor", "sarah@example.com", true);
        samplePatient.setId(10L);
    }

    @Test
    @DisplayName("Device Registration: generates 6-digit pairing code with 5-minute validity")
    void testRegisterDeviceGeneratesCode() {
        when(deviceDao.save(any(VrDevice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VrDevice device = devicePairingService.registerDevice(samplePatient, "Meta Quest 3", DeviceType.HEADSET, true, true);

        assertNotNull(device.getPairingCode());
        assertEquals(6, device.getPairingCode().length());
        assertEquals(DeviceStatus.PAIRING, device.getStatus());
        assertTrue(device.isPairingCodeValid());
    }

    @Test
    @DisplayName("Pairing Verification: binds client successfully when pairing code is active")
    void testPairDeviceSuccess() {
        VrDevice device = new VrDevice("Meta Quest 3", DeviceType.HEADSET, samplePatient.getId());
        device.setId(1L);
        device.setPairingCode("123456");
        device.setPairingCodeExpiresAt(LocalDateTime.now().plusMinutes(4));
        device.setStatus(DeviceStatus.PAIRING);

        when(deviceDao.findByPairingCode("123456")).thenReturn(Optional.of(device));

        VrDevice paired = devicePairingService.pairDeviceWithCode("123456", "OculusBrowser/32.0", "192.168.1.50");

        assertEquals(DeviceStatus.CONNECTED, paired.getStatus());
        assertEquals("192.168.1.50", paired.getIpAddress());
        assertTrue(VrConnectionRegistry.getInstance().isDeviceActive("123456"));
    }

    @Test
    @DisplayName("Pairing Expiration: rejects pairing code when expired beyond 5-minute window")
    void testPairDeviceExpiredThrowsValidationException() {
        VrDevice device = new VrDevice("Meta Quest 3", DeviceType.HEADSET, samplePatient.getId());
        device.setId(2L);
        device.setPairingCode("123456");
        device.setPairingCodeExpiresAt(LocalDateTime.now().minusMinutes(1)); // Expired

        when(deviceDao.findByPairingCode("123456")).thenReturn(Optional.of(device));

        assertThrows(ValidationException.class, () ->
                devicePairingService.pairDeviceWithCode("123456", "ClientUA", "127.0.0.1")
        );
    }

    @Test
    @DisplayName("Heartbeat: refreshes last seen timestamp for active device")
    void testRecordHeartbeat() {
        devicePairingService.recordHeartbeat(5L);
        verify(deviceDao).updateHeartbeat(eq(5L), any(LocalDateTime.class));
    }
}
