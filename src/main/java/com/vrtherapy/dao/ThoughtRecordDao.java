package com.vrtherapy.dao;

import com.vrtherapy.model.ThoughtRecord;

import java.util.List;

public interface ThoughtRecordDao extends GenericDao<ThoughtRecord, Long> {

    List<ThoughtRecord> findByPatientId(Long patientId);
}
