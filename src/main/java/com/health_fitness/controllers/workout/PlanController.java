package com.health_fitness.controllers.workout;


import com.health_fitness.model.workout.Plan;
import com.health_fitness.model.workout.PlanSession;
import com.health_fitness.services.workout.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @GetMapping("/{planId}")
    public Plan getPlan(@PathVariable int planId) {
        return planService.getPlan(planId);
    }

    @GetMapping("/{planId}/sessions")
    public List<PlanSession> getPlanSessions(@PathVariable int planId) {
        return planService.getPlanSession(planId);
    }
}