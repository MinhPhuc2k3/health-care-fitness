package com.health_fitness.services.workout;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.nutrition.MenuPlan;
import com.health_fitness.model.user.HealthInfo;
import com.health_fitness.model.workout.Plan;
import com.health_fitness.model.workout.PlanSession;
import com.health_fitness.repository.workout.PlanRepository;
import com.health_fitness.services.user.HealthInfoService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
@Transactional
public class PlanService {
    private final PlanRepository planRepository;

    public List<PlanSession> getPlanSession(int planId) {
        return getPlan(planId).getPlanSessions();
    }

    @PreAuthorize("isAuthenticated()")
    public Plan getPlan(int planId) {
        return planRepository.findById(planId).orElseThrow(() -> new NotFoundException("Plan not found"));
    }
}