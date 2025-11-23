package com.health_fitness.repository.quiz;

import com.health_fitness.model.quiz.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Integer> {
}
