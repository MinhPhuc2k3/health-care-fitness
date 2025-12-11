package com.health_fitness.controllers.workout.dto;

import com.health_fitness.model.user.User;
import com.health_fitness.model.workout.Exercise;
import com.health_fitness.model.workout.MuscleGroup;
import lombok.Data;

import java.util.List;

@Data
public class ExerciseSearchDTO {
    List<MuscleGroup> muscleGroups;
    Exercise.ExerciseCategory category;
    User.ActivityLevel activityLevel;
}
