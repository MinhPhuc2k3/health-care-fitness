package com.health_fitness.repository.workout;

import com.health_fitness.model.workout.PlanSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanSessionRepository extends JpaRepository<PlanSession, Integer> {
}
