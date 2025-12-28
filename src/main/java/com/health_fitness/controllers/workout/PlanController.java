package com.health_fitness.controllers.workout;


import com.health_fitness.model.workout.Plan;
import com.health_fitness.model.workout.PlanSession;
import com.health_fitness.services.workout.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @PostMapping
    public  Plan createPlan(@RequestBody Plan plan){
        return planService.createPlan(plan);
    }

    @GetMapping
    public List<Plan> getAllPlan(@RequestParam int goalId){
        return planService.getAll(goalId);
    }

    @GetMapping("/{planId}")
    public Plan getPlan(@PathVariable int planId) {
        return planService.getPlan(planId);
    }

    @GetMapping("/{planId}/sessions")
    public List<PlanSession> getPlanSessions(@PathVariable int planId) {
        return planService.getPlanSession(planId);
    }

    @PutMapping("/{planId}")
    public Plan updatePlan(@PathVariable int planId, @RequestBody Plan plan) {
        return planService.updatePlan(planId, plan);
    }

    @DeleteMapping("/{planId}")
    public boolean removePlan(@PathVariable int planId) {
        return planService.removePlan(planId);
    }
}