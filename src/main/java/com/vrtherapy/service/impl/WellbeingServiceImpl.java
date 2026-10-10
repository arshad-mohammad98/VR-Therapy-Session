package com.vrtherapy.service.impl;

import com.vrtherapy.dao.BadgeDao;
import com.vrtherapy.dao.HomeworkDao;
import com.vrtherapy.dao.MessageDao;
import com.vrtherapy.dao.MoodDao;
import com.vrtherapy.dao.ThoughtRecordDao;
import com.vrtherapy.dao.impl.BadgeDaoImpl;
import com.vrtherapy.dao.impl.HomeworkDaoImpl;
import com.vrtherapy.dao.impl.MessageDaoImpl;
import com.vrtherapy.dao.impl.MoodDaoImpl;
import com.vrtherapy.dao.impl.ThoughtRecordDaoImpl;
import com.vrtherapy.exception.ResourceNotFoundException;
import com.vrtherapy.exception.ValidationException;
import com.vrtherapy.model.Badge;
import com.vrtherapy.model.Homework;
import com.vrtherapy.model.Message;
import com.vrtherapy.model.MoodEntry;
import com.vrtherapy.model.ThoughtRecord;
import com.vrtherapy.model.User;
import com.vrtherapy.service.WellbeingService;
import com.vrtherapy.strategy.BadgeContext;
import com.vrtherapy.strategy.BadgeEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for Patient wellbeing workflows: Mood journaling,
 * CBT cognitive restructuring worksheets, inter-session homework, gamification badges,
 * and direct therapist messaging.
 *
 * Demonstrates:
 * - Strategy Pattern (BadgeEngine integration)
 * - Java Collections & Streams
 */
public class WellbeingServiceImpl implements WellbeingService {

    private static final Logger logger = LoggerFactory.getLogger(WellbeingServiceImpl.class);

    private final MoodDao moodDao;
    private final ThoughtRecordDao thoughtRecordDao;
    private final HomeworkDao homeworkDao;
    private final BadgeDao badgeDao;
    private final MessageDao messageDao;
    private final BadgeEngine badgeEngine;

    public WellbeingServiceImpl() {
        this(new MoodDaoImpl(), new ThoughtRecordDaoImpl(), new HomeworkDaoImpl(), new BadgeDaoImpl(), new MessageDaoImpl(), new BadgeEngine());
    }

    public WellbeingServiceImpl(MoodDao moodDao, ThoughtRecordDao thoughtRecordDao,
                                HomeworkDao homeworkDao, BadgeDao badgeDao,
                                MessageDao messageDao, BadgeEngine badgeEngine) {
        this.moodDao = moodDao;
        this.thoughtRecordDao = thoughtRecordDao;
        this.homeworkDao = homeworkDao;
        this.badgeDao = badgeDao;
        this.messageDao = messageDao;
        this.badgeEngine = badgeEngine;
    }

    @Override
    public MoodEntry logMood(User patient, int moodScore, int energyScore, String emotionTags, String notes) {
        if (moodScore < 1 || moodScore > 10) {
            throw new ValidationException("Mood rating must be an integer between 1 and 10.");
        }
        MoodEntry entry = new MoodEntry(patient.getId(), moodScore, emotionTags != null ? emotionTags.trim() : "Calm", notes);
        MoodEntry saved = moodDao.save(entry);
        logger.info("Patient {} logged mood score {} ({})", patient.getUsername(), moodScore, emotionTags);
        return saved;
    }

    @Override
    public List<MoodEntry> getMoodHistory(User patient) {
        if (patient == null) {
            return List.of();
        }
        return moodDao.findByPatientIdOrderByRecordedAtDesc(patient.getId());
    }

    @Override
    public ThoughtRecord recordThought(User patient, String situation, String automaticThought, String emotion,
                                        String cognitiveDistortion, String alternativeThought, int outcomeRating) {
        if (situation == null || situation.trim().isEmpty()) {
            throw new ValidationException("Situation description cannot be empty.");
        }
        if (automaticThought == null || automaticThought.trim().isEmpty()) {
            throw new ValidationException("Automatic thought cannot be empty.");
        }

        ThoughtRecord record = new ThoughtRecord(
                patient.getId(),
                situation.trim(),
                automaticThought.trim(),
                cognitiveDistortion != null ? cognitiveDistortion.trim() : "All-or-Nothing Thinking",
                alternativeThought != null ? alternativeThought.trim() : "",
                Math.max(0, Math.min(100, outcomeRating))
        );

        ThoughtRecord saved = thoughtRecordDao.save(record);
        logger.info("Patient {} recorded CBT Thought Record for situation: {}", patient.getUsername(), situation);
        return saved;
    }

    @Override
    public List<ThoughtRecord> getThoughtRecords(User patient) {
        if (patient == null) {
            return List.of();
        }
        return thoughtRecordDao.findByPatientId(patient.getId());
    }

    @Override
    public Homework assignHomework(User therapist, Long patientId, String title, String description, String category, LocalDate dueDate) {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Homework title cannot be empty.");
        }
        LocalDate finalDue = dueDate != null ? dueDate : LocalDate.now().plusDays(7);
        Homework homework = new Homework(patientId, therapist.getId(), title.trim(), description, finalDue);
        Homework saved = homeworkDao.save(homework);
        logger.info("Assigned homework '{}' to patient ID {} by therapist {}", title, patientId, therapist.getUsername());
        return saved;
    }

    @Override
    public Homework completeHomework(Long homeworkId, User patient, String notes) {
        Homework homework = homeworkDao.findById(homeworkId)
                .orElseThrow(() -> new ResourceNotFoundException("Homework not found: " + homeworkId));

        homework.setCompleted(true);
        if (notes != null && !notes.trim().isEmpty()) {
            homework.setDescription(homework.getDescription() + "\n[Patient Completion Reflection: " + notes.trim() + "]");
        }
        homeworkDao.update(homework);
        logger.info("Patient {} marked homework #{} as completed", patient.getUsername(), homeworkId);
        return homework;
    }

    @Override
    public List<Homework> getHomeworkForPatient(User patient) {
        if (patient == null) {
            return List.of();
        }
        return homeworkDao.findByPatientId(patient.getId());
    }

    @Override
    public List<Homework> getHomeworkAssignedByTherapist(User therapist) {
        if (therapist == null) {
            return List.of();
        }
        return homeworkDao.findByTherapistId(therapist.getId());
    }

    @Override
    public List<Badge> evaluateAndAwardBadges(User patient) {
        if (patient == null) {
            return List.of();
        }
        List<Badge> currentBadges = badgeDao.findByPatientId(patient.getId());
        Set<String> ownedCodes = currentBadges.stream()
                .map(Badge::getBadgeType)
                .collect(Collectors.toSet());

        // Construct clinical context for badge evaluation
        BadgeContext context = new BadgeContext(
                patient,
                currentBadges.size() + 1, // approximate completed sessions
                1, // streak days
                1, // hierarchy levels cleared
                20.0 // best improvement pct
        );

        List<Badge> newBadges = badgeEngine.evaluateNewBadges(context, ownedCodes);
        for (Badge b : newBadges) {
            b.setPatientId(patient.getId());
            badgeDao.save(b);
            logger.info("Awarded badge '{}' ({}) to patient {}", b.getTitle(), b.getBadgeType(), patient.getUsername());
        }
        return newBadges;
    }

    @Override
    public List<Badge> getBadgesForPatient(User patient) {
        if (patient == null) {
            return List.of();
        }
        return badgeDao.findByPatientId(patient.getId());
    }

    @Override
    public Message sendMessage(User sender, Long recipientId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new ValidationException("Message text cannot be empty.");
        }
        Message msg = new Message(sender.getId(), recipientId, content.trim());
        Message saved = messageDao.save(msg);
        logger.info("Sent message from {} to recipient ID {}", sender.getUsername(), recipientId);
        return saved;
    }

    @Override
    public List<Message> getConversation(User user1, User user2) {
        if (user1 == null || user2 == null) {
            return List.of();
        }
        messageDao.markConversationAsRead(user2.getId(), user1.getId());
        return messageDao.findConversation(user1.getId(), user2.getId());
    }

    @Override
    public long getUnreadMessageCount(User recipient) {
        if (recipient == null) {
            return 0L;
        }
        return messageDao.countUnreadMessages(recipient.getId());
    }
}
