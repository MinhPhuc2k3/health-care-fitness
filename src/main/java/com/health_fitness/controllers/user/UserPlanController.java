package com.health_fitness.controllers.user;

import com.health_fitness.model.user.UserPlan;
import com.health_fitness.model.workout.Plan;
import com.health_fitness.services.workout.UserPlanService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-plans")
@RequiredArgsConstructor
public class UserPlanController {

    private final UserPlanService userPlanService;

    @GetMapping("/active")
    public UserPlan getActiveUserGoal() {
        return userPlanService.getActiveUserPlan();
    }

    @PostMapping
    public UserPlan addUserGoal(@Valid @RequestBody Plan plan) {
        return userPlanService.addUserPlan(plan);
    }
}