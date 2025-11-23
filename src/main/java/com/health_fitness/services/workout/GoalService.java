package com.health_fitness.services.workout;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.user.Goal;
import com.health_fitness.model.workout.Plan;
import com.health_fitness.repository.user.GoalRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;

    public List<Goal> getListGoal() {
        return goalRepository.findAll();
    }

    public List<Plan> getListPlan(int goalId) {
        return getGoal(goalId).getPlans();
    }

    public Goal getGoal(int goalId) {
        return goalRepository.findById(goalId).orElseThrow(()->new NotFoundException("Goal not found"));
    }
}
