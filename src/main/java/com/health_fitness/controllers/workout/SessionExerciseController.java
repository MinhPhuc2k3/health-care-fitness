package com.health_fitness.controllers.workout;

import com.health_fitness.model.workout.Exercise;
import com.health_fitness.model.workout.SessionExercise;
import com.health_fitness.services.workout.SessionExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/session-exercises")
@RequiredArgsConstructor
public class SessionExerciseController {

    private final SessionExerciseService sessionExerciseService;

    @GetMapping("/{sessionExerciseId}")
    public SessionExercise getSessionExercise(@PathVariable int sessionExerciseId) {
        return sessionExerciseService.getSessionExercise(sessionExerciseId);
    }

    @PostMapping
    public SessionExercise createSessionExercise(
            @RequestBody @Valid SessionExercise sessionExercise) {
        return sessionExerciseService.createSessionExercise(sessionExercise);
    }

    @PutMapping("/{sessionExerciseId}")
    public SessionExercise updateSessionExercise(
            @PathVariable int sessionExerciseId,
            @Valid @RequestBody SessionExercise sessionExercise) {

        sessionExerciseService.updateSessionExercise(sessionExerciseId, sessionExercise);
        return sessionExerciseService.getSessionExercise(sessionExerciseId);
    }

    @DeleteMapping("/{sessionExerciseId}")
    public void deleteSessionExercise(@PathVariable int sessionExerciseId) {
        sessionExerciseService.deleteSessionExercise(sessionExerciseId);
    }

    @GetMapping("/{sessionExerciseId}/exercise")
    public Exercise getExercise(@PathVariable int sessionExerciseId) {
        return sessionExerciseService.getExercise(sessionExerciseId);
    }
}
