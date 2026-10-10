package com.vrtherapy.listener;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * HTTP Session Listener tracking active web user sessions.
 * Demonstrates thread-safe AtomicInteger counters across web sessions.
 */
@WebListener
public class AppSessionListener implements HttpSessionListener {

    private static final Logger logger = LoggerFactory.getLogger(AppSessionListener.class);
    private static final AtomicInteger ACTIVE_SESSIONS = new AtomicInteger(0);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        int count = ACTIVE_SESSIONS.incrementAndGet();
        logger.info("Session created: ID={}, Total Active Web Sessions={}", se.getSession().getId(), count);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        int count = ACTIVE_SESSIONS.decrementAndGet();
        if (count < 0) {
            ACTIVE_SESSIONS.set(0);
        }
        logger.info("Session invalidated: ID={}, Total Active Web Sessions={}", se.getSession().getId(), ACTIVE_SESSIONS.get());
    }

    public static int getActiveSessionsCount() {
        return ACTIVE_SESSIONS.get();
    }
}
