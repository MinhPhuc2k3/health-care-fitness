package com.health_fitness.services.workout;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.user.User;
import com.health_fitness.model.workout.Exercise;
import com.health_fitness.model.workout.MuscleGroup;
import com.health_fitness.repository.workout.ExerciseRepository;
import com.health_fitness.repository.workout.specification.ExerciseSpecification;
import com.health_fitness.utils.ImageUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Service
@Transactional
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final MuscleGroupService muscleGroupService;
    private final ImageUtils imageUtils;

    public Exercise getExercise(int exerciseId){
        return exerciseRepository.findById(exerciseId).orElseThrow(()->new NotFoundException("Exercise not found"));
    }

    public Page<Exercise> getAllExercise(Pageable pageable){
        return exerciseRepository.findAll(pageable);
    }

    public Page<Exercise> getListExerciseByCategoryMuscleGroup(Exercise.ExerciseCategory category, List<Integer> muscleGroupIds, User.ActivityLevel activityLevel, Pageable pageable) {
        java.util.List<MuscleGroup> muscleGroup = muscleGroupService.getMuscleGroupByIds(muscleGroupIds);
        return exerciseRepository.findAll(ExerciseSpecification.findByCategoryMuscleGroupsActivityLevel(category, muscleGroup, activityLevel), pageable);
    }

    @PreAuthorize("isAuthenticated()")
    public Exercise createExercise(Exercise exercise) throws IOException{
        Exercise exerciseToSave = new Exercise();
        exerciseToSave.setCategory(exercise.getCategory());
        exerciseToSave.setDescription(exercise.getDescription());
        exerciseToSave.setDifficulty(exercise.getDifficulty());
        exerciseToSave.setUnit(exercise.getUnit());
        exerciseToSave.setDefaultCaloriesPerUnit(exercise.getDefaultCaloriesPerUnit());
        exerciseToSave.setName(exercise.getName());
        if(exercise.getMuscleGroups()!=null) exerciseToSave.setMuscleGroups(muscleGroupService.getMuscleGroupByIds(exercise.getMuscleGroups().stream().map(MuscleGroup::getId).toList()));
        MultipartFile file = exercise.getImageFile();
        if (file != null && !file.isEmpty()) {
            List<Object> uploadResult = imageUtils.uploadImage(file, ImageUtils.ImageType.EXERCISE);
            exerciseToSave.setImageId((String) uploadResult.get(0));
            exerciseToSave.setImageUrl((String) uploadResult.get(1));
        }
        return exerciseRepository.save(exerciseToSave);
    }
}
