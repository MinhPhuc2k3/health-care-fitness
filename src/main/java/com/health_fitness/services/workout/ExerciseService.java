package com.health_fitness.services.workout;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.user.User;
import com.health_fitness.model.workout.Exercise;
import com.health_fitness.model.workout.MuscleGroup;
import com.health_fitness.repository.workout.ExerciseRepository;
import com.health_fitness.repository.workout.MuscleGroupRepository;
import com.health_fitness.repository.workout.specification.ExerciseSpecification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
@Transactional
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final MuscleGroupRepository muscleGroupRepository;

    public Exercise getExercise(int exerciseId){
        return exerciseRepository.findById(exerciseId).orElseThrow(()->new NotFoundException("Exercise not found"));
    }

    public Page<Exercise> getAllExercise(Pageable pageable){
        return exerciseRepository.findAll(pageable);
    }

    public Page<Exercise> getListExerciseByCategoryMuscleGroup(Exercise.ExerciseCategory category, List<Integer> muscleGroupIds, User.ActivityLevel activityLevel, Pageable pageable) {
        java.util.List<MuscleGroup> muscleGroup = muscleGroupRepository.findAllById(muscleGroupIds);
        return exerciseRepository.findAll(ExerciseSpecification.findByCategoryMuscleGroupsActivityLevel(category, muscleGroup, activityLevel), pageable);
    }
}
