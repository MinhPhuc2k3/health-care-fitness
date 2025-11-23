package com.health_fitness.repository.quiz;

import com.health_fitness.model.quiz.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
}
