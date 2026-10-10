package com.vrtherapy.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe multi-session command queue coordinating real-time therapist controls
 * and patient WebXR AJAX polling consumers.
 *
 * Demonstrates:
 * - Multithreading & Synchronization (ConcurrentHashMap, BlockingQueue, ReentrantLock)
 * - Safe concurrent access across multiple concurrent live sessions
 */
public class CommandQueue {

    private static final Logger logger = LoggerFactory.getLogger(CommandQueue.class);
    private static final CommandQueue INSTANCE = new CommandQueue();

    private final Map<Long, BlockingQueue<VrCommand>> sessionQueues = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    private CommandQueue() {
    }

    public static CommandQueue getInstance() {
        return INSTANCE;
    }

    /**
     * Enqueues a command directed to the specified live session room.
     */
    public boolean enqueueCommand(Long liveSessionId, VrCommand command) {
        if (liveSessionId == null || command == null) {
            return false;
        }
        lock.lock();
        try {
            BlockingQueue<VrCommand> queue = sessionQueues.computeIfAbsent(liveSessionId, k -> new LinkedBlockingQueue<>(50));
            boolean offered = queue.offer(command);
            if (offered) {
                logger.info("Enqueued command [{}] for session {}", command.getType(), liveSessionId);
            } else {
                logger.warn("Command queue full for session {}", liveSessionId);
            }
            return offered;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Polls the next available command, waiting briefly if the queue is empty.
     */
    public VrCommand pollNextCommand(Long liveSessionId, long timeoutMs) {
        BlockingQueue<VrCommand> queue = sessionQueues.get(liveSessionId);
        if (queue == null) {
            return null;
        }
        try {
            return queue.poll(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * Drains all queued commands for a live session in a thread-safe atomic manner.
     */
    public List<VrCommand> drainAllCommands(Long liveSessionId) {
        List<VrCommand> commands = new ArrayList<>();
        BlockingQueue<VrCommand> queue = sessionQueues.get(liveSessionId);
        if (queue != null) {
            queue.drainTo(commands);
        }
        return commands;
    }

    /**
     * Removes the queue for a concluded session to prevent memory leaks.
     */
    public void cleanupSession(Long liveSessionId) {
        lock.lock();
        try {
            sessionQueues.remove(liveSessionId);
            logger.info("Cleaned up command queue for session {}", liveSessionId);
        } finally {
            lock.unlock();
        }
    }
}
