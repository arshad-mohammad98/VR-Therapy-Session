package com.vrtherapy.dao;

import com.vrtherapy.model.Homework;

import java.util.List;

public interface HomeworkDao extends GenericDao<Homework, Long> {

    List<Homework> findByPatientId(Long patientId);

    List<Homework> findByTherapistId(Long therapistId);

    void setCompleted(Long homeworkId, boolean completed);

    List<Homework> findPendingReminders();

    void markReminderSent(Long homeworkId);
}
