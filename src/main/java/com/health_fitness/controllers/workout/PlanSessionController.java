package com.health_fitness.controllers.workout;

import com.health_fitness.model.workout.PlanSession;
import com.health_fitness.services.workout.PlanSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/plan-sessions")
@RequiredArgsConstructor
public class PlanSessionController {

    private final PlanSessionService planSessionService;

    @GetMapping("/{planSessionId}")
    public PlanSession getPlanSession(@PathVariable int planSessionId) {
        return planSessionService.getPlanSession(planSessionId);
    }
}