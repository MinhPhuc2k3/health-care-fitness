package com.health_fitness.repository.workout;

import com.health_fitness.model.user.User;
import com.health_fitness.model.workout.Exercise;
import com.health_fitness.model.workout.MuscleGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {
    @Query("""
     SELECT e FROM Exercise e
     WHERE e.category =:category
     """)
    Page<Exercise> getListExerciseByCategory(Exercise.ExerciseCategory category, Pageable pageable);

    @Query("""
     SELECT e FROM Exercise e JOIN e.muscleGroups mg
     WHERE e.category =:category AND mg IN :muscleGroup AND e.difficulty = :activityLevel
     """)
    Page<Exercise> getListExerciseByCategoryMuscleGroup(Exercise.ExerciseCategory category, List<MuscleGroup> muscleGroup, User.ActivityLevel activityLevel, Pageable pageable);
}