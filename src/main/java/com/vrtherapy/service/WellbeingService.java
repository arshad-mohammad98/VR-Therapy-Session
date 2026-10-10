package com.vrtherapy.service;

import com.vrtherapy.model.Badge;
import com.vrtherapy.model.Homework;
import com.vrtherapy.model.Message;
import com.vrtherapy.model.MoodEntry;
import com.vrtherapy.model.ThoughtRecord;
import com.vrtherapy.model.User;
import java.time.LocalDate;
import java.util.List;

/**
 * Service managing patient wellbeing tools: daily mood journals, CBT thought records,
 * therapeutic homework, gamified badge evaluation, and clinical messages.
 */
public interface WellbeingService {

    MoodEntry logMood(User patient, int moodScore, int energyScore, String emotionTags, String notes);

    List<MoodEntry> getMoodHistory(User patient);

    ThoughtRecord recordThought(User patient, String situation, String automaticThought, String emotion,
                                String cognitiveDistortion, String alternativeThought, int outcomeRating);

    List<ThoughtRecord> getThoughtRecords(User patient);

    Homework assignHomework(User therapist, Long patientId, String title, String description, String category, LocalDate dueDate);

    Homework completeHomework(Long homeworkId, User patient, String notes);

    List<Homework> getHomeworkForPatient(User patient);

    List<Homework> getHomeworkAssignedByTherapist(User therapist);

    List<Badge> evaluateAndAwardBadges(User patient);

    List<Badge> getBadgesForPatient(User patient);

    Message sendMessage(User sender, Long recipientId, String content);

    List<Message> getConversation(User user1, User user2);

    long getUnreadMessageCount(User recipient);
}
