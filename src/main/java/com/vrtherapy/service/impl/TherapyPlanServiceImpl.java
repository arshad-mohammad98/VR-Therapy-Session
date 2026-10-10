package com.vrtherapy.service.impl;

import com.vrtherapy.dao.ExposureLevelDao;
import com.vrtherapy.dao.PlanDao;
import com.vrtherapy.dao.impl.ExposureLevelDaoImpl;
import com.vrtherapy.dao.impl.PlanDaoImpl;
import com.vrtherapy.model.ConditionType;
import com.vrtherapy.model.EnvironmentType;
import com.vrtherapy.model.ExposureLevel;
import com.vrtherapy.model.TherapyPlan;
import com.vrtherapy.model.User;
import com.vrtherapy.service.TherapyPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation managing multi-week therapy plans and automated
 * progressive hierarchy unlock logic.
 *
 * Demonstrates:
 * - Algorithmic exposure therapy unlock logic in Java
 * - Safe transaction and DAO orchestration
 */
public class TherapyPlanServiceImpl implements TherapyPlanService {

    private static final Logger logger = LoggerFactory.getLogger(TherapyPlanServiceImpl.class);
    private final PlanDao planDao;
    private final ExposureLevelDao exposureLevelDao;

    public TherapyPlanServiceImpl() {
        this(new PlanDaoImpl(), new ExposureLevelDaoImpl());
    }

    public TherapyPlanServiceImpl(PlanDao planDao, ExposureLevelDao exposureLevelDao) {
        this.planDao = planDao;
        this.exposureLevelDao = exposureLevelDao;
    }

    @Override
    public TherapyPlan createPlan(User therapist, Long patientId, String title, String clinicalGoal, ConditionType condition, int totalWeeks) {
        TherapyPlan plan = new TherapyPlan(
                patientId,
                therapist.getId(),
                title.trim(),
                condition,
                Math.max(1, totalWeeks),
                clinicalGoal != null ? clinicalGoal.trim() : ""
        );
        return planDao.save(plan);
    }

    @Override
    public ExposureLevel addExposureLevel(Long planId, int levelNumber, String title, String description,
                                          EnvironmentType environmentType, int intensity, int unlockThresholdStress) {
        boolean unlocked = (levelNumber == 1); // First tier always starts unlocked
        ExposureLevel level = new ExposureLevel(
                planId,
                null,
                levelNumber,
                title.trim(),
                description != null ? description.trim() : "",
                unlockThresholdStress,
                unlocked,
                "Configured for " + environmentType.getDisplayName() + " (Intensity " + intensity + ")"
        );
        return exposureLevelDao.save(level);
    }

    @Override
    public boolean evaluateAndUnlockNextLevel(Long planId, Long currentLevelId, int postSessionStress) {
        Optional<ExposureLevel> optCurrent = exposureLevelDao.findById(currentLevelId);
        if (optCurrent.isEmpty()) {
            return false;
        }

        ExposureLevel current = optCurrent.get();
        logger.info("Evaluating hierarchy unlock: current level {}, threshold {}, patient stress {}",
                current.getLevelOrder(), current.getDistressRatingThreshold(), postSessionStress);

        // Core clinical check: distress must be <= threshold
        if (postSessionStress <= current.getDistressRatingThreshold()) {
            List<ExposureLevel> allLevels = exposureLevelDao.findByPlanIdOrderByLevelOrder(planId);
            for (ExposureLevel nextCandidate : allLevels) {
                if (nextCandidate.getLevelOrder() == current.getLevelOrder() + 1 && !nextCandidate.isUnlocked()) {
                    exposureLevelDao.unlockLevel(nextCandidate.getId());
                    logger.info("Successfully unlocked exposure hierarchy Level {} ({}) for plan {}",
                            nextCandidate.getLevelOrder(), nextCandidate.getTitle(), planId);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<TherapyPlan> getPlansForTherapist(User therapist) {
        if (therapist == null) {
            return planDao.findAll();
        }
        List<TherapyPlan> plans = planDao.findByTherapistId(therapist.getId());
        for (TherapyPlan p : plans) {
            p.setExposureLevels(exposureLevelDao.findByPlanIdOrderByLevelOrder(p.getId()));
        }
        return plans;
    }

    @Override
    public List<TherapyPlan> getPlansForPatient(User patient) {
        if (patient == null) {
            return List.of();
        }
        List<TherapyPlan> plans = planDao.findByPatientId(patient.getId());
        for (TherapyPlan p : plans) {
            p.setExposureLevels(exposureLevelDao.findByPlanIdOrderByLevelOrder(p.getId()));
        }
        return plans;
    }

    @Override
    public Optional<TherapyPlan> getPlanById(Long planId) {
        Optional<TherapyPlan> opt = planDao.findById(planId);
        opt.ifPresent(p -> p.setExposureLevels(exposureLevelDao.findByPlanIdOrderByLevelOrder(p.getId())));
        return opt;
    }

    @Override
    public List<ExposureLevel> getLevelsForPlan(Long planId) {
        return exposureLevelDao.findByPlanIdOrderByLevelOrder(planId);
    }
}
