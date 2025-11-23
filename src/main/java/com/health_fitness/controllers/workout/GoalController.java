package com.health_fitness.controllers.workout;

import com.health_fitness.model.user.Goal;
import com.health_fitness.model.workout.Plan;
import com.health_fitness.services.workout.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @GetMapping
    public List<Goal> getListGoals() {
        return goalService.getListGoal();
    }

    @GetMapping("/{goalId}")
    public Goal getGoal(@PathVariable int goalId) {
        return goalService.getGoal(goalId);
    }

    @GetMapping("/{goalId}/plans")
    public List<Plan> getListPlans(@PathVariable int goalId) {
        return goalService.getListPlan(goalId);
    }
}
