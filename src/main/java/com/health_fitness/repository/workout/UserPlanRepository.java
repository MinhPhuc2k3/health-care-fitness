package com.health_fitness.repository.workout;

import com.health_fitness.model.user.UserPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPlanRepository extends JpaRepository<UserPlan, Integer> {
}
