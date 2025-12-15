package com.health_fitness.services.workout;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.workout.MuscleGroup;
import com.health_fitness.model.workout.Plan;
import com.health_fitness.model.workout.PlanSession;
import com.health_fitness.repository.workout.PlanRepository;
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

    private final GoalService goalService;

    private final MuscleGroupService muscleGroupService;

    public List<PlanSession> getPlanSession(int planId) {
        return getPlan(planId).getPlanSessions();
    }

    @PreAuthorize("isAuthenticated()")
    public Plan getPlan(int planId) {
        return planRepository.findById(planId).orElseThrow(() -> new NotFoundException("Plan not found"));
    }

    @PreAuthorize("isAuthenticated()")
    public Plan createPlan(Plan plan) {
        Plan planToSave = new Plan();
        planToSave.setName(plan.getName());
        planToSave.setGoal(goalService.getGoal(plan.getGoal().getId()));
        for(PlanSession planSession: plan.getPlanSessions()) {
            PlanSession planSessionToSave = new PlanSession();
            planSessionToSave.setPlan(planToSave);
            planSessionToSave.setCategory(planSession.getCategory());
            planSessionToSave.setSessionDayOfWeek(planSession.getSessionDayOfWeek());
            List<MuscleGroup> muscleGroups= muscleGroupService.getMuscleGroupByIds(planSession.getMuscleGroups().stream().map(MuscleGroup::getId).toList());
            planSessionToSave.setMuscleGroups(muscleGroups);
            planToSave.getPlanSessions().add(planSession);
        }
        return planRepository.save(plan);
    }
}