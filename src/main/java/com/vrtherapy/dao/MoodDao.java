package com.vrtherapy.dao;

import com.vrtherapy.model.MoodEntry;

import java.util.List;

public interface MoodDao extends GenericDao<MoodEntry, Long> {

    List<MoodEntry> findByPatientIdOrderByRecordedAtDesc(Long patientId);

    List<MoodEntry> findRecentByPatientId(Long patientId, int limit);
}
