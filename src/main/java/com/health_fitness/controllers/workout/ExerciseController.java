package com.health_fitness.controllers.workout;

import com.health_fitness.model.user.User;
import com.health_fitness.model.workout.Exercise;
import com.health_fitness.services.workout.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping("/{exerciseId}")
    public Exercise getExercise(@PathVariable int exerciseId) {
        return exerciseService.getExercise(exerciseId);
    }

    @GetMapping
    public Page<Exercise> getListExercises(
            @RequestParam Exercise.ExerciseCategory category, @RequestParam List<Integer> muscleGroup, @RequestParam User.ActivityLevel activityLevel,
            @PageableDefault(size = 20) Pageable pageable) {

        return exerciseService.getListExerciseByCategoryMuscleGroup(
                category, muscleGroup, activityLevel, pageable
        );
    }

    @PostMapping
    public Exercise createExercise(@ModelAttribute Exercise exercise) throws IOException {
        return exerciseService.createExercise(exercise);
    }
}
