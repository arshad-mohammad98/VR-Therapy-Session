package com.vrtherapy.service;

import com.vrtherapy.dao.ExposureLevelDao;
import com.vrtherapy.dao.PlanDao;
import com.vrtherapy.model.ExposureLevel;
import com.vrtherapy.model.TherapyPlan;
import com.vrtherapy.service.impl.TherapyPlanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExposureHierarchyUnlockTest {

    @Mock
    private PlanDao planDao;

    @Mock
    private ExposureLevelDao exposureLevelDao;

    @InjectMocks
    private TherapyPlanServiceImpl therapyPlanService;

    private ExposureLevel level1;
    private ExposureLevel level2;

    @BeforeEach
    void setUp() {
        level1 = new ExposureLevel(100L, null, 1, "Balcony Low", "2nd floor view", 4, true, "Notes");
        level1.setId(1L);

        level2 = new ExposureLevel(100L, null, 2, "High Roof", "10th floor observation", 3, false, "Notes");
        level2.setId(2L);
    }

    @Test
    @DisplayName("Hierarchy Unlock: unlocks Level 2 when Level 1 post-session stress drops below threshold")
    void testUnlockSubsequentLevelOnLowStress() {
        when(exposureLevelDao.findById(1L)).thenReturn(Optional.of(level1));
        when(exposureLevelDao.findByPlanIdOrderByLevelOrder(100L)).thenReturn(List.of(level1, level2));

        // Patient reports post-stress = 3 (threshold is <= 4)
        boolean unlocked = therapyPlanService.evaluateAndUnlockNextLevel(100L, 1L, 3);

        assertTrue(unlocked, "Subsequent level should unlock when stress <= threshold");
        verify(exposureLevelDao).unlockLevel(2L);
    }

    @Test
    @DisplayName("Hierarchy Lock: remains locked when post-session stress exceeds threshold")
    void testRemainLockedWhenStressHigh() {
        when(exposureLevelDao.findById(1L)).thenReturn(Optional.of(level1));

        // Patient reports post-stress = 7 (threshold is <= 4)
        boolean unlocked = therapyPlanService.evaluateAndUnlockNextLevel(100L, 1L, 7);

        assertFalse(unlocked, "Subsequent level should NOT unlock when stress remains high");
        verify(exposureLevelDao, never()).unlockLevel(anyLong());
    }
}
