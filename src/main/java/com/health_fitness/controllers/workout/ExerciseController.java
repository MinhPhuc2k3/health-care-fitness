package com.health_fitness.controllers.workout;

import com.health_fitness.model.user.User;
import com.health_fitness.model.workout.Exercise;
import com.health_fitness.model.workout.dto.ExerciseBulkImportResponse;
import com.health_fitness.services.workout.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestParam(required = false) String exerciseName,
            @RequestParam(required = false) Exercise.ExerciseCategory category, @RequestParam(required = false) List<Integer> muscleGroup, @RequestParam(required = false) User.ActivityLevel activityLevel,
            @PageableDefault(size = 20) Pageable pageable) {

        return exerciseService.getListExerciseByCategoryMuscleGroup(
                exerciseName,  category, muscleGroup, activityLevel, pageable
        );
    }

    @PostMapping
    public Exercise createExercise(@ModelAttribute Exercise exercise) throws IOException {
        return exerciseService.createExercise(exercise);
    }

    @GetMapping("/import/template")
    public ResponseEntity<byte[]> downloadTemplate() throws IOException {
        byte[] file = exerciseService.generateImportTemplate();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exercise_import_template.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExerciseBulkImportResponse> importExercise(
            @RequestPart("file") MultipartFile excelFile,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {
        return ResponseEntity.ok(exerciseService.importExercises(excelFile, images));
    }

    @PutMapping("/{exerciseId}")
    public ResponseEntity<?> updateExercise(
            @PathVariable int exerciseId,
            @RequestPart(value = "exercise") Exercise exercise,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        try {
            if (image != null) {
                exercise.setImageFile(image);
            }
            Exercise updated = exerciseService.updateExercise(exerciseId, exercise);
            return ResponseEntity.ok(updated);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating exercise: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{exerciseId}")
    public ResponseEntity<?> deleteExercise(@PathVariable int exerciseId) {
        try {
            exerciseService.deleteExercise(exerciseId);
            return ResponseEntity.ok("Exercise deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error deleting exercise: " + e.getMessage());
        }
    }
}
