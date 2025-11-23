package com.health_fitness.services.workout;

import com.health_fitness.exception.BadRequestException;
import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.workout.Exercise;
import com.health_fitness.model.workout.SessionExercise;
import com.health_fitness.repository.workout.SessionExerciseRepository;
import com.health_fitness.services.user.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Transactional
public class SessionExerciseService {
    public SessionExerciseRepository sessionExerciseRepository;
    public SessionService sessionService;
    public ExerciseService exerciseService;
    public UserService userService;

    @PreAuthorize("isAuthenticated()")
    public SessionExercise createSessionExercise(SessionExercise sessionExercise){
        Exercise exercise = exerciseService.getExercise(sessionExercise.getExercise().getId());
        if(userService.getUser().getActivityLevel().ordinal() < exercise.difficulty.ordinal())
            throw new BadRequestException("Exercise difficulty doesn't not suitable for you");
        SessionExercise sessionExerciseToSave = SessionExercise.builder()
                .exercise(exercise)
                .session(sessionService.getSession(sessionExercise.getSession().getId()))
                .sets(sessionExercise.getSets())
                .reps(sessionExercise.getReps())
                .weightUsed(sessionExercise.getWeightUsed())
                .build();

        return sessionExerciseRepository.save(sessionExerciseToSave);
    }

    public Exercise getExercise(int sessionExerciseId){
        return getSessionExercise(sessionExerciseId).getExercise();
    }

    public void deleteSessionExercise(int sessionExerciseId) {
        sessionExerciseRepository.deleteById(sessionExerciseId);
    }

    public void updateSessionExercise(int sessionExerciseId, SessionExercise sessionExercise){
        SessionExercise oldSessionExercise = getSessionExercise(sessionExerciseId);
        BeanUtils.copyProperties(sessionExercise, oldSessionExercise, "id", "session");
    }

    public SessionExercise getSessionExercise(int sessionExerciseId){
        return sessionExerciseRepository.findById(sessionExerciseId).orElseThrow(()-> new NotFoundException("SessionExercise not found"));
    }
}
