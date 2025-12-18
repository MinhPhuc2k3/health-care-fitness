package com.health_fitness.repository.workout;

import com.health_fitness.model.workout.RunSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RunSessionRepository extends JpaRepository<RunSession, Long> {

    List<RunSession> findByCreatedBy_Id(Integer userId);
}

