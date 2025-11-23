package com.health_fitness.services.workout;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.workout.Exercise;
import com.health_fitness.repository.workout.ExerciseRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Transactional
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;

    public Exercise getExercise(int exerciseId){
        return exerciseRepository.findById(exerciseId).orElseThrow(()->new NotFoundException("Exercise not found"));
    }

    public Page<Exercise> getAllExercise(Pageable pageable){
        return exerciseRepository.findAll(pageable);
    }

    public Page<Exercise> getListExerciseByCategoryMuscleGroup(Exercise.ExerciseCategory category, Exercise.MuscleGroup muscleGroup, Pageable pageable) {
        if(category==null && muscleGroup==null){
            return getAllExercise(pageable);
        }
        return (muscleGroup==null)? exerciseRepository.getListExerciseByCategory(category, pageable) : exerciseRepository.getListExerciseByCategoryMuscleGroup(category,muscleGroup,pageable);
    }
}
