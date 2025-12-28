package com.health_fitness.services.workout;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.user.Goal;
import com.health_fitness.model.workout.MuscleGroup;
import com.health_fitness.model.workout.Plan;
import com.health_fitness.model.workout.PlanSession;
import com.health_fitness.repository.workout.PlanRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
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

    @PreAuthorize("isAuthenticated()")
    public Plan updatePlan(int planId, Plan plan) {
        Plan planToSave = getPlan(planId);
        if(plan.getName().isBlank()) planToSave.setName(plan.getName());
        if(plan.getGoal()!=null) planToSave.setGoal(plan.getGoal());
        if(plan.getPlanSessions()!=null && !plan.getPlanSessions().isEmpty()){
            planToSave.getPlanSessions().clear();
            planToSave.getPlanSessions().addAll(plan.getPlanSessions());
        }
        return planRepository.save(planToSave);
    }

    @PreAuthorize("isAuthenticated()")
    public boolean removePlan(int planId) {
        planRepository.deleteById(planId);
        return true;
    }

    public List<Plan> getAll(int goalId) {
        Goal goal = goalService.getGoal(goalId);
        Specification<Plan> planSpecification = new Specification<Plan>() {
            @Override
            public Predicate toPredicate(Root<Plan> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if(goal!=null){
                    return cb.equal(root.get("goal"), goal);
                }
                return null;
            }
        };
        return planRepository.findAll(planSpecification);
    }
}