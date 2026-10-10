package com.vrtherapy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Utility for local area network (LAN) IPv4 discovery, Subject Alternative Name (SAN) generation,
 * and mobile connection URL generation for secure WebXR device access.
 */
public final class NetworkUtil {

    private static final Logger logger = LoggerFactory.getLogger(NetworkUtil.class);
    private static final String DEFAULT_FALLBACK_IP = "127.0.0.1";

    private NetworkUtil() {
    }

    /**
     * Discovers the machine's primary active non-loopback LAN IPv4 address (e.g. 192.168.x.x, 10.x.x.x).
     *
     * @return primary LAN IPv4 address string, or 127.0.0.1 if none is detected
     */
    public static String getPrimaryLanIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            List<String> candidates = new ArrayList<>();

            for (NetworkInterface ni : Collections.list(interfaces)) {
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) {
                    continue;
                }

                String name = ni.getName().toLowerCase();
                String displayName = ni.getDisplayName().toLowerCase();

                // Skip common hypervisor and virtual adapter names
                if (displayName.contains("virtual") || displayName.contains("vbox") ||
                        displayName.contains("vmware") || displayName.contains("wsl") ||
                        displayName.contains("hyper-v") || name.startsWith("docker") ||
                        name.startsWith("veth")) {
                    continue;
                }

                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress() && !addr.isLinkLocalAddress()) {
                        String hostAddress = addr.getHostAddress();
                        candidates.add(hostAddress);
                    }
                }
            }

            if (!candidates.isEmpty()) {
                // Prefer 192.168.x.x or 10.x.x.x
                for (String ip : candidates) {
                    if (ip.startsWith("192.168.") || ip.startsWith("10.")) {
                        return ip;
                    }
                }
                return candidates.get(0);
            }

            InetAddress localHost = InetAddress.getLocalHost();
            if (localHost instanceof Inet4Address && !localHost.isLoopbackAddress()) {
                return localHost.getHostAddress();
            }
        } catch (SocketException | RuntimeException ex) {
            logger.warn("Socket error while enumerating network interfaces: {}", ex.getMessage());
        } catch (Exception ex) {
            logger.warn("Could not determine local host IP: {}", ex.getMessage());
        }

        return DEFAULT_FALLBACK_IP;
    }

    /**
     * Builds the Subject Alternative Name (SAN) extension parameter string for Java Keytool,
     * including localhost, 127.0.0.1, and the detected LAN IP.
     *
     * @param lanIp the detected LAN IP address
     * @return SAN string, e.g. "dns:localhost,ip:127.0.0.1,ip:192.168.0.237"
     */
    public static String buildSanParameter(String lanIp) {
        String effectiveIp = (lanIp != null && !lanIp.isBlank()) ? lanIp.trim() : DEFAULT_FALLBACK_IP;
        if (effectiveIp.equals(DEFAULT_FALLBACK_IP) || effectiveIp.equals("localhost")) {
            return "dns:localhost,ip:127.0.0.1";
        }
        return "dns:localhost,ip:127.0.0.1,ip:" + effectiveIp;
    }

    /**
     * Builds a full HTTP URL with context path.
     */
    public static String getHttpUrl(String host, int port, String contextPath) {
        String base = "http://" + host + (port == 80 ? "" : ":" + port);
        return formatUrl(base, contextPath);
    }

    /**
     * Builds a full HTTPS URL with context path.
     */
    public static String getHttpsUrl(String host, int port, String contextPath) {
        String base = "https://" + host + (port == 443 ? "" : ":" + port);
        return formatUrl(base, contextPath);
    }

    /**
     * Generates a public QR code image URL for easily scanning with mobile phones / Meta Quest headsets.
     */
    public static String getQrCodeImageUrl(String targetUrl, int size) {
        if (targetUrl == null || targetUrl.isBlank()) {
            return "";
        }
        String encoded = URLEncoder.encode(targetUrl, StandardCharsets.UTF_8);
        return "https://api.qrserver.com/v1/create-qr-code/?size=" + size + "x" + size + "&data=" + encoded;
    }

    private static String formatUrl(String base, String contextPath) {
        if (contextPath == null || contextPath.isBlank() || "/".equals(contextPath)) {
            return base;
        }
        if (!contextPath.startsWith("/")) {
            contextPath = "/" + contextPath;
        }
        return base + contextPath;
    }
}
