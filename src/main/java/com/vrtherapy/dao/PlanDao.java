package com.vrtherapy.dao;

import com.vrtherapy.model.TherapyPlan;

import java.util.List;

public interface PlanDao extends GenericDao<TherapyPlan, Long> {

    List<TherapyPlan> findByPatientId(Long patientId);

    List<TherapyPlan> findByTherapistId(Long therapistId);
}
