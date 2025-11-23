package com.health_fitness.repository.workout;

import com.health_fitness.model.workout.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {
    @Query("""
     SELECT e FROM Exercise e
     WHERE e.category =:category
     """)
    Page<Exercise> getListExerciseByCategory(Exercise.ExerciseCategory category, Pageable pageable);

    @Query("""
     SELECT e FROM Exercise e
     WHERE e.category =:category AND e.muscleGroup =: muscleGroup
     """)
    Page<Exercise> getListExerciseByCategoryMuscleGroup(Exercise.ExerciseCategory category, Exercise.MuscleGroup muscleGroup, Pageable pageable);
}