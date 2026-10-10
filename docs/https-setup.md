# HTTPS & WebXR Secure Origin Configuration Guide

**Project:** Virtual Reality Therapy Sessions: Connected VR Mental Health Platform  
**Target:** Jakarta Servlet 6.0 / Apache Tomcat 10.1  
**Protocols:** HTTP (8080) and HTTPS (8443)  

---

## 1. Why HTTPS is Required for WebXR & Mobile VR
Modern mobile and VR headset browsers (Meta Quest Browser, Google Chrome on Android, and Apple Safari on iOS) enforce the W3C WebXR Device API security specification:
- **Stereoscopic 3D & Immersive VR (`navigator.xr.requestSession('immersive-vr')`):** Blocked on insecure origins (`http://` over local network IPs).
- **Device Sensors (`DeviceOrientationEvent`, `DeviceMotionEvent`):** Accelerometer and gyroscope streams are completely disabled on unencrypted LAN origins.
- **Microphone / Spatial Audio:** Hardware access requires a secure origin.

Localhost (`http://localhost:8080`) is treated as a secure origin by desktop browsers, but accessing the server from a phone or VR headset via your LAN IP (`http://192.168.0.x:8080`) is **insecure** and requires **HTTPS**.

---

## 2. Generating the PKCS12 Certificate Keystore

The project includes automated scripts that detect your machine's active local IP address and create a 2048-bit RSA certificate valid for 365 days with the appropriate **Subject Alternative Names (SAN)**:

### On Windows:
```cmd
scripts\generate-cert.bat
```

### On Linux / macOS:
```bash
chmod +x scripts/generate-cert.sh
./scripts/generate-cert.sh
```

This creates:
- `src/main/resources/vrtherapy.p12`
- `vrtherapy.p12` (at project root)
- Keystore alias: `vrtherapy`
- Keystore password: `vrtherapy123`
- SAN extensions: `dns:localhost,ip:127.0.0.1,ip:<your-lan-ip>`

---

## 3. Option A: Embedded Tomcat (Single Command)

If running the project via the built-in launcher (`.\mvnw.cmd compile exec:java`), `MainServer.java` **automatically** detects `vrtherapy.p12` and initializes the HTTPS connector on port **8443** alongside HTTP port **8080**.

---

## 4. Option B: Standalone Apache Tomcat 10.1 Setup

If you deploy `target/vr-therapy-sessions.war` into an external Apache Tomcat 10.1 installation, configure the HTTPS connector in your Tomcat `conf/server.xml` file.

### Ready-to-Copy `server.xml` Connector Snippet
Open `conf/server.xml` in your Tomcat 10.1 installation and add the following `<Connector>` block inside the `<Service name="Catalina">` element:

```xml
    <!-- Standard HTTP Connector (Port 8080) -->
    <Connector port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" />

    <!-- Secure HTTPS / TLS Connector for WebXR Sensor Access (Port 8443) -->
    <Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
               maxThreads="150" SSLEnabled="true" scheme="https" secure="true">
        <SSLHostConfig hostName="_default_">
            <Certificate certificateKeystoreFile="conf/vrtherapy.p12"
                         certificateKeystorePassword="vrtherapy123"
                         certificateKeystoreType="PKCS12"
                         certificateKeyAlias="vrtherapy"
                         type="RSA" />
        </SSLHostConfig>
    </Connector>
```

> **Note:** Copy the generated `vrtherapy.p12` file into Tomcat's `conf/` directory (or update `certificateKeystoreFile` with its absolute path).

---

## 5. Firewall Configuration (Windows)

To allow incoming connections from your phone or Meta Quest headset on port 8443, run this in an administrative PowerShell terminal:

```powershell
New-NetFirewallRule -DisplayName "VR Therapy HTTPS (Port 8443)" -Direction Inbound -LocalPort 8443 -Protocol TCP -Action Allow
New-NetFirewallRule -DisplayName "VR Therapy HTTP (Port 8080)" -Direction Inbound -LocalPort 8080 -Protocol TCP -Action Allow
```

---

## 6. Accessing from Phone or Meta Quest Headset

1. Ensure your PC and phone/headset are on the same Wi-Fi network.
2. In the mobile or Quest browser, navigate to:  
   👉 **`https://<your-lan-ip>:8443/`**  
   *(e.g., `https://192.168.0.237:8443/vr/launch`)*
3. **Bypassing the Self-Signed Certificate Warning:**
   - **Chrome on Android:** Tap **"Advanced"** &rarr; Tap **"Proceed to 192.168.0.x (unsafe)"**.
   - **Safari on iOS:** Tap **"Show Details"** &rarr; Tap **"visit this website"** &rarr; Confirm with passcode/FaceID.
   - **Meta Quest Browser:** Tap **"Advanced"** &rarr; Tap **"Continue to site (unsafe)"**.
4. Once loaded over HTTPS, tap **"Enter Virtual Reality"**. WebXR will request sensor permissions and activate full stereoscopic immersion without blocking!
