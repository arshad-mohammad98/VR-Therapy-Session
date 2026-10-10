package com.vrtherapy.listener;

import com.vrtherapy.thread.HeartbeatMonitor;
import com.vrtherapy.thread.ReminderService;
import com.vrtherapy.util.ConnectionManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Servlet Context Listener responsible for bootstrapping background services,
 * connection pooling, and managing thread pool lifecycles.
 *
 * Demonstrates:
 * - Servlet Context Lifecycle Integration
 * - Multithreading & Synchronization (ScheduledExecutorService, ExecutorService)
 * - Graceful executor shutdown
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);

    public static final String TELEMETRY_EXECUTOR_ATTR = "TELEMETRY_EXECUTOR";
    public static final String SCHEDULED_EXECUTOR_ATTR = "SCHEDULED_EXECUTOR";

    private ScheduledExecutorService scheduledExecutor;
    private ExecutorService telemetryExecutor;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("==================================================================");
        logger.info("Initializing Virtual Reality Therapy Platform on Apache Tomcat 10");
        logger.info("==================================================================");

        ServletContext context = sce.getServletContext();

        // 1. Initialize ConnectionManager and verify DB connection
        ConnectionManager cm = ConnectionManager.getInstance();
        logger.info("Database Connection Pool successfully initialized.");

        // 2. Initialize Telemetry Simulator Executor pool
        telemetryExecutor = Executors.newFixedThreadPool(10, r -> {
            Thread t = new Thread(r, "VR-TelemetrySim-Worker");
            t.setDaemon(true);
            return t;
        });
        context.setAttribute(TELEMETRY_EXECUTOR_ATTR, telemetryExecutor);

        // 3. Initialize Scheduled Background Tasks (Heartbeat Monitor & Reminder Service)
        scheduledExecutor = Executors.newScheduledThreadPool(2, r -> {
            Thread t = new Thread(r, "VR-Scheduler-Daemon");
            t.setDaemon(true);
            return t;
        });
        context.setAttribute(SCHEDULED_EXECUTOR_ATTR, scheduledExecutor);

        // Schedule HeartbeatMonitor every 10 seconds
        scheduledExecutor.scheduleAtFixedRate(new HeartbeatMonitor(30), 5, 10, TimeUnit.SECONDS);
        logger.info("Registered HeartbeatMonitor task (interval: 10s).");

        // Schedule ReminderService every 60 seconds
        scheduledExecutor.scheduleAtFixedRate(new ReminderService(), 15, 60, TimeUnit.SECONDS);
        logger.info("Registered ReminderService task (interval: 60s).");

        // 4. Discover and log primary LAN IP endpoints for phone and VR headset access
        String lanIp = com.vrtherapy.util.NetworkUtil.getPrimaryLanIp();
        String contextPath = context.getContextPath();
        String httpUrl = com.vrtherapy.util.NetworkUtil.getHttpUrl(lanIp, 8080, contextPath);
        String httpsUrl = com.vrtherapy.util.NetworkUtil.getHttpsUrl(lanIp, 8443, contextPath);
        String vrLaunchUrl = com.vrtherapy.util.NetworkUtil.getHttpsUrl(lanIp, 8443, (contextPath == null ? "" : contextPath) + "/vr/launch");
        String qrCodeUrl = com.vrtherapy.util.NetworkUtil.getQrCodeImageUrl(vrLaunchUrl, 200);

        context.setAttribute("LAN_IP", lanIp);
        context.setAttribute("HTTP_URL", httpUrl);
        context.setAttribute("HTTPS_URL", httpsUrl);
        context.setAttribute("VR_LAUNCH_URL", vrLaunchUrl);
        context.setAttribute("QR_CODE_URL", qrCodeUrl);

        logger.info("------------------------------------------------------------------");
        logger.info("DEVICE ACCESS URLS (Local Network):");
        logger.info("  -> Phone / Headset HTTP:         {}", httpUrl);
        logger.info("  -> Phone / Headset HTTPS:        {}", httpsUrl);
        logger.info("  -> Direct WebXR Launch (Secure): {}", vrLaunchUrl);
        logger.info("------------------------------------------------------------------");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Shutting down Virtual Reality Therapy Platform background executors...");

        // 1. Shutdown scheduled background executor
        if (scheduledExecutor != null) {
            shutdownExecutorGracefully(scheduledExecutor, "ScheduledExecutor");
        }

        // 2. Shutdown telemetry simulation executor
        if (telemetryExecutor != null) {
            shutdownExecutorGracefully(telemetryExecutor, "TelemetryExecutor");
        }

        // 3. Shutdown connection pool
        ConnectionManager.getInstance().shutdown();
        logger.info("VR Therapy Platform successfully terminated.");
    }

    private void shutdownExecutorGracefully(ExecutorService executor, String name) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                logger.warn("{} did not terminate in 3 seconds; forcing shutdown.", name);
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
