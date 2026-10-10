package com.vrtherapy.thread;

import com.vrtherapy.dao.HomeworkDao;
import com.vrtherapy.dao.MessageDao;
import com.vrtherapy.dao.impl.HomeworkDaoImpl;
import com.vrtherapy.dao.impl.MessageDaoImpl;
import com.vrtherapy.model.Homework;
import com.vrtherapy.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Scheduled daemon service periodically scanning pending homework and therapeutic commitments.
 *
 * Demonstrates:
 * - Multithreading via ScheduledExecutorService
 * - Automated background reminder dispatch
 */
public class ReminderService implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ReminderService.class);

    private final HomeworkDao homeworkDao;
    private final MessageDao messageDao;

    public ReminderService() {
        this(new HomeworkDaoImpl(), new MessageDaoImpl());
    }

    public ReminderService(HomeworkDao homeworkDao, MessageDao messageDao) {
        this.homeworkDao = homeworkDao;
        this.messageDao = messageDao;
    }

    @Override
    public void run() {
        try {
            List<Homework> dueSoon = homeworkDao.findPendingReminders();
            for (Homework hw : dueSoon) {
                logger.info("Sending reminder for homework '{}' to patient {}", hw.getTitle(), hw.getPatientId());
                homeworkDao.markReminderSent(hw.getId());

                // Generate system reminder message to patient
                String reminderText = "Automated Reminder: Your homework task \"" + hw.getTitle() +
                        "\" is due on " + hw.getDueDate() + ". Remember to log your reflection!";
                Message reminderMsg = new Message(hw.getTherapistId(), hw.getPatientId(), reminderText);
                messageDao.save(reminderMsg);
            }
        } catch (Exception e) {
            logger.error("Error in ReminderService execution: {}", e.getMessage(), e);
        }
    }
}
