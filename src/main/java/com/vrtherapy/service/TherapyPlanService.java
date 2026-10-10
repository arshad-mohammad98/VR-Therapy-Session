package com.vrtherapy.service;

import com.vrtherapy.model.ConditionType;
import com.vrtherapy.model.EnvironmentType;
import com.vrtherapy.model.ExposureLevel;
import com.vrtherapy.model.TherapyPlan;
import com.vrtherapy.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Service managing multi-week clinical treatment plans and graduated exposure hierarchy ladders.
 */
public interface TherapyPlanService {

    TherapyPlan createPlan(User therapist, Long patientId, String title, String clinicalGoal, ConditionType condition, int totalWeeks);

    ExposureLevel addExposureLevel(Long planId, int levelNumber, String title, String description,
                                   EnvironmentType environmentType, int intensity, int unlockThresholdStress);

    boolean evaluateAndUnlockNextLevel(Long planId, Long currentLevelId, int postSessionStress);

    List<TherapyPlan> getPlansForTherapist(User therapist);

    List<TherapyPlan> getPlansForPatient(User patient);

    Optional<TherapyPlan> getPlanById(Long planId);

    List<ExposureLevel> getLevelsForPlan(Long planId);
}
