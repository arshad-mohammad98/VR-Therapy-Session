package com.vrtherapy.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetworkUtilTest {

    @Test
    @DisplayName("Should detect a valid primary LAN IP address or fallback")
    void testGetPrimaryLanIp() {
        String lanIp = NetworkUtil.getPrimaryLanIp();
        assertNotNull(lanIp);
        assertFalse(lanIp.isBlank());
        // Verify IPv4 structure (contains dots)
        assertTrue(lanIp.contains("."));
    }

    @Test
    @DisplayName("Should build valid Subject Alternative Name (SAN) string with localhost and LAN IP")
    void testBuildSanParameter() {
        String san = NetworkUtil.buildSanParameter("192.168.0.237");
        assertNotNull(san);
        assertTrue(san.contains("dns:localhost"));
        assertTrue(san.contains("ip:127.0.0.1"));
        assertTrue(san.contains("ip:192.168.0.237"));

        // Fallback case
        String fallbackSan = NetworkUtil.buildSanParameter("127.0.0.1");
        assertTrue(fallbackSan.contains("dns:localhost"));
        assertTrue(fallbackSan.contains("ip:127.0.0.1"));
    }

    @Test
    @DisplayName("Should construct valid HTTP and HTTPS URLs with context path")
    void testUrlGeneration() {
        String httpUrl = NetworkUtil.getHttpUrl("192.168.0.237", 8080, "");
        assertTrue(httpUrl.startsWith("http://192.168.0.237:8080"));

        String httpsUrl = NetworkUtil.getHttpsUrl("192.168.0.237", 8443, "/vrtherapy");
        assertTrue(httpsUrl.startsWith("https://192.168.0.237:8443/vrtherapy"));
    }

    @Test
    @DisplayName("Should generate valid QR code API image URL")
    void testQrCodeUrlGeneration() {
        String qrUrl = NetworkUtil.getQrCodeImageUrl("https://192.168.0.237:8443/vr/launch", 200);
        assertNotNull(qrUrl);
        assertTrue(qrUrl.contains("api.qrserver.com"));
        assertTrue(qrUrl.contains("200x200"));
    }
}
