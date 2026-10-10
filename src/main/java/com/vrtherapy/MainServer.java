package com.vrtherapy;

import com.vrtherapy.util.NetworkUtil;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;

import java.io.File;

/**
 * Embedded Tomcat 10 launcher for the "Virtual Reality Therapy Sessions: Connected VR Mental Health Platform".
 * <p>
 * Supports both HTTP (default: 8080) and HTTPS (default: 8443) for WebXR device sensors.
 * <pre>
 *   .\mvnw.cmd compile exec:java
 * </pre>
 */
public class MainServer {

    private static final int DEFAULT_HTTP_PORT = 8080;
    private static final int DEFAULT_HTTPS_PORT = 8443;
    private static final String DEFAULT_KEYSTORE_PASSWORD = "vrtherapy123";

    public static void main(String[] args) throws Exception {
        int httpPort = DEFAULT_HTTP_PORT;
        String portProperty = System.getProperty("server.port");
        String portEnv = System.getenv("PORT");

        if (portProperty != null && !portProperty.isBlank()) {
            try {
                httpPort = Integer.parseInt(portProperty.trim());
            } catch (NumberFormatException ignored) {}
        } else if (portEnv != null && !portEnv.isBlank()) {
            try {
                httpPort = Integer.parseInt(portEnv.trim());
            } catch (NumberFormatException ignored) {}
        }

        int httpsPort = DEFAULT_HTTPS_PORT;
        String httpsPortProp = System.getProperty("server.https.port");
        if (httpsPortProp != null && !httpsPortProp.isBlank()) {
            try {
                httpsPort = Integer.parseInt(httpsPortProp.trim());
            } catch (NumberFormatException ignored) {}
        }

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(httpPort);
        tomcat.setBaseDir("target/tomcat");
        tomcat.getConnector(); // Initialize the default HTTP connector

        // Check for self-signed PKCS12 keystore for WebXR HTTPS
        File keystoreFile = findKeystoreFile();
        boolean httpsEnabled = false;

        if (keystoreFile != null && keystoreFile.exists()) {
            try {
                Connector httpsConnector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
                httpsConnector.setPort(httpsPort);
                httpsConnector.setSecure(true);
                httpsConnector.setScheme("https");

                Http11NioProtocol protocol = (Http11NioProtocol) httpsConnector.getProtocolHandler();
                protocol.setSSLEnabled(true);

                SSLHostConfig sslHostConfig = new SSLHostConfig();
                sslHostConfig.setHostName("_default_");
                SSLHostConfigCertificate cert = new SSLHostConfigCertificate(sslHostConfig, SSLHostConfigCertificate.Type.RSA);
                cert.setCertificateKeystoreFile(keystoreFile.getAbsolutePath());
                cert.setCertificateKeystorePassword(DEFAULT_KEYSTORE_PASSWORD);
                cert.setCertificateKeystoreType("PKCS12");
                cert.setCertificateKeyAlias("vrtherapy");

                sslHostConfig.addCertificate(cert);
                httpsConnector.addSslHostConfig(sslHostConfig);

                tomcat.getService().addConnector(httpsConnector);
                httpsEnabled = true;
            } catch (Exception ex) {
                System.err.println("[WARN] Failed to initialize HTTPS connector on port " + httpsPort + ": " + ex.getMessage());
            }
        }

        File webappDir = new File("src/main/webapp");
        if (!webappDir.exists()) {
            webappDir = new File("webapp");
        }
        if (!webappDir.exists()) {
            webappDir = new File(".");
        }

        StandardContext ctx = (StandardContext) tomcat.addWebapp("", webappDir.getAbsolutePath());
        ctx.setParentClassLoader(MainServer.class.getClassLoader());
        ctx.setReloadable(false);

        // Bind compiled classes from target/classes to WEB-INF/classes so servlets and annotations are loaded
        File additionWebInfClasses = new File("target/classes");
        if (additionWebInfClasses.exists()) {
            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    additionWebInfClasses.getAbsolutePath(), "/"));
            ctx.setResources(resources);
        }

        String lanIp = NetworkUtil.getPrimaryLanIp();

        System.out.println("===============================================================================");
        System.out.println("   VIRTUAL REALITY THERAPY SESSIONS: CONNECTED VR MENTAL HEALTH PLATFORM       ");
        System.out.println("   Jakarta Servlet 6.0 | JSP 3.1 | JSTL 3.0 | Raw JDBC Architecture             ");
        System.out.println("===============================================================================");
        System.out.println("   -> Local HTTP:          http://localhost:" + httpPort + "/");
        if (httpsEnabled) {
            System.out.println("   -> Local HTTPS (Secure): https://localhost:" + httpsPort + "/");
            System.out.println("   -> Phone/Quest HTTPS:   https://" + lanIp + ":" + httpsPort + "/");
            System.out.println("   -> WebXR Launch (VR):   https://" + lanIp + ":" + httpsPort + "/vr/launch");
        } else {
            System.out.println("   -> Phone/Quest HTTP:    http://" + lanIp + ":" + httpPort + "/");
            System.out.println("   [NOTICE] Run scripts/generate-cert.bat to enable HTTPS on port " + httpsPort + " for WebXR sensors.");
        }
        System.out.println("   -> Device Simulator:    http://localhost:" + httpPort + "/simulator");
        System.out.println("   -> Default Therapist:   therapist1 / therapist123");
        System.out.println("   -> Default Patient:     patient1 / patient123");
        System.out.println("   -> Default Admin:       admin / admin123");
        System.out.println("===============================================================================");

        tomcat.start();
        tomcat.getServer().await();
    }

    private static File findKeystoreFile() {
        File[] candidates = new File[] {
                new File("src/main/resources/vrtherapy.p12"),
                new File("vrtherapy.p12"),
                new File("target/classes/vrtherapy.p12")
        };
        for (File candidate : candidates) {
            if (candidate.exists()) {
                return candidate;
            }
        }
        return null;
    }
}
