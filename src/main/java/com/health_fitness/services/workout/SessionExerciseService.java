package com.health_fitness.services.workout;

import com.health_fitness.exception.BadRequestException;
import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.workout.Exercise;
import com.health_fitness.model.workout.SessionExercise;
import com.health_fitness.repository.workout.SessionExerciseRepository;
import com.health_fitness.services.user.HealthInfoService;
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
    public HealthInfoService healthInfoService;

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
        if(exercise.getUnit() == Exercise.Unit.MET){
            sessionExerciseToSave.setEstimatedCalories(exercise.getDefaultCaloriesPerUnit()*sessionExercise.getHours()*healthInfoService.findLastestHealthInfo().getWeight());
        }else{
            sessionExerciseToSave.setEstimatedCalories(exercise.getDefaultCaloriesPerUnit()*sessionExercise.getReps()*sessionExercise.getSets());
        }
        sessionExerciseToSave.getSession().addEstimatedCalories(sessionExerciseToSave.getEstimatedCalories());
        return sessionExerciseRepository.save(sessionExerciseToSave);
    }
    @PreAuthorize("isAuthenticated()")
    public Exercise getExercise(int sessionExerciseId){
        return getSessionExercise(sessionExerciseId).getExercise();
    }
    @PreAuthorize("isAuthenticated()")
    public void deleteSessionExercise(int sessionExerciseId) {
        SessionExercise sessionExercise = getSessionExercise(sessionExerciseId);
        sessionExercise.getSession().addEstimatedCalories(sessionExercise.getEstimatedCalories()==null? 0:-sessionExercise.getEstimatedCalories());
        sessionExerciseRepository.deleteById(sessionExerciseId);
    }
    @PreAuthorize("isAuthenticated()")
    public SessionExercise updateSessionExercise(int sessionExerciseId, SessionExercise sessionExercise){
        SessionExercise oldSessionExercise = getSessionExercise(sessionExerciseId);
        Exercise exercise = oldSessionExercise.getExercise();
        float delta = 0;
        if(exercise.getUnit() == Exercise.Unit.MET){
            sessionExercise.setEstimatedCalories(exercise.getDefaultCaloriesPerUnit()*sessionExercise.getHours()*healthInfoService.findLastestHealthInfo().getWeight());
        }else{
            sessionExercise.setEstimatedCalories(exercise.getDefaultCaloriesPerUnit()*sessionExercise.getReps()*sessionExercise.getSets());
        }
        delta = sessionExercise.getEstimatedCalories() - oldSessionExercise.getEstimatedCalories();
        oldSessionExercise.getSession().addEstimatedCalories(delta);
        BeanUtils.copyProperties(sessionExercise, oldSessionExercise, "id", "session", "exercise");
        return sessionExerciseRepository.save(oldSessionExercise);
    }

    public SessionExercise getSessionExercise(int sessionExerciseId){
        return sessionExerciseRepository.findById(sessionExerciseId).orElseThrow(()-> new NotFoundException("SessionExercise not found"));
    }
}
