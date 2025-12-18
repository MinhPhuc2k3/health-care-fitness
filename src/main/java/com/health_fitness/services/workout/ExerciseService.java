package com.health_fitness.services.workout;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.user.User;
import com.health_fitness.model.workout.Exercise;
import com.health_fitness.model.workout.MuscleGroup;
import com.health_fitness.model.workout.dto.ExerciseBulkImportResponse;
import com.health_fitness.repository.workout.ExerciseRepository;
import com.health_fitness.repository.workout.specification.ExerciseSpecification;
import com.health_fitness.utils.ImageUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

    public Page<Exercise> getListExerciseByCategoryMuscleGroup(String exerciseName, Exercise.ExerciseCategory category, List<Integer> muscleGroupIds, User.ActivityLevel activityLevel, Pageable pageable) {
        java.util.List<MuscleGroup> muscleGroup = (muscleGroupIds==null)? null : muscleGroupService.getMuscleGroupByIds(muscleGroupIds);
        return exerciseRepository.findAll(ExerciseSpecification.findByCategoryMuscleGroupsActivityLevel(exerciseName, category, muscleGroup, activityLevel), pageable);
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

    public byte[] generateImportTemplate() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Exercises");

        // Header
        Row header = sheet.createRow(0);
        String[] headers = {
                "name", "description", "category", "difficulty",
                "unit", "defaultCaloriesPerUnit", "muscleGroups", "imageFileName"
        };

        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
            sheet.autoSizeColumn(i);
        }

        // Dropdown Category
        String[] categories = Arrays.stream(Exercise.ExerciseCategory.values())
                .map(Enum::name).toArray(String[]::new);

        addDropdown(sheet, categories, 1, 1000, 2);

        // Dropdown Difficulty
        String[] difficulties = Arrays.stream(User.ActivityLevel.values())
                .map(Enum::name).toArray(String[]::new);

        addDropdown(sheet, difficulties, 1, 1000, 3);

        // Dropdown Unit
        String[] units = Arrays.stream(Exercise.Unit.values())
                .map(Enum::name).toArray(String[]::new);

        addDropdown(sheet, units, 1, 1000, 4);

        // MuscleGroup dropdown (ID)
        List<MuscleGroup> muscleGroups = muscleGroupService.getMuscleGroup();

        String[] muscleGroupOptions = muscleGroups.stream()
                .map(m -> m.getId() + " - " + m.getName())
                .toArray(String[]::new);

        addDropdown(sheet, muscleGroupOptions, 1, 1000, 6);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    private void addDropdown(Sheet sheet, String[] values, int firstRow, int lastRow, int col) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createExplicitListConstraint(values);
        CellRangeAddressList range = new CellRangeAddressList(firstRow, lastRow, col, col);
        DataValidation validation = helper.createValidation(constraint, range);
        validation.setSuppressDropDownArrow(true);
        sheet.addValidationData(validation);
    }

    public ExerciseBulkImportResponse importExercises(
            MultipartFile excelFile,
            List<MultipartFile> images
    ) throws IOException {

        Workbook workbook = WorkbookFactory.create(excelFile.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        // Map imageName -> MultipartFile
        Map<String, MultipartFile> imageMap = new HashMap<>();
        if (images != null) {
            for (MultipartFile img : images) {
                imageMap.put(img.getOriginalFilename(), img);
            }
        }

        List<Exercise> saved = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                Exercise exercise = new Exercise();
                exercise.setName(row.getCell(0).getStringCellValue());
                exercise.setDescription(row.getCell(1).getStringCellValue());

                exercise.setCategory(
                        Exercise.ExerciseCategory.valueOf(row.getCell(2).getStringCellValue())
                );

                exercise.setDifficulty(
                        User.ActivityLevel.valueOf(row.getCell(3).getStringCellValue())
                );

                exercise.setUnit(
                        Exercise.Unit.valueOf(row.getCell(4).getStringCellValue())
                );

                exercise.setDefaultCaloriesPerUnit(
                        (float) row.getCell(5).getNumericCellValue()
                );

                // Muscle groups
                String muscleCellValue = row.getCell(6).getStringCellValue();
                String[] values = muscleCellValue.split(",");

                List<Integer> muscleGroupIds = Arrays.stream(values)
                        .map(String::trim)
                        .map(v -> v.split("-")[0].trim())
                        .map(Integer::parseInt)
                        .toList();

                exercise.setMuscleGroups(
                        muscleGroupService.getMuscleGroupByIds(muscleGroupIds)
                );

                // Image
                String imageName = row.getCell(7).getStringCellValue();
                MultipartFile image = imageMap.get(imageName);

                if (image != null) {
                    List<Object> upload = imageUtils.uploadImage(image, ImageUtils.ImageType.EXERCISE);
                    exercise.setImageId((String) upload.get(0));
                    exercise.setImageUrl((String) upload.get(1));
                }

                saved.add(exerciseRepository.save(exercise));

            } catch (Exception e) {
                errors.add("Row " + (i + 1) + ": " + e.getMessage());
            }
        }

        return new ExerciseBulkImportResponse(saved.size(), errors.size(),errors);
    }

    @PreAuthorize("isAuthenticated()")
    public Exercise updateExercise(int exerciseId, Exercise exerciseUpdate) throws IOException {
        Exercise existingExercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NotFoundException("Exercise not found"));

        // Update basic fields
        if (exerciseUpdate.getName() != null) {
            existingExercise.setName(exerciseUpdate.getName());
        }
        if (exerciseUpdate.getDescription() != null) {
            existingExercise.setDescription(exerciseUpdate.getDescription());
        }
        if (exerciseUpdate.getCategory() != null) {
            existingExercise.setCategory(exerciseUpdate.getCategory());
        }
        if (exerciseUpdate.getDifficulty() != null) {
            existingExercise.setDifficulty(exerciseUpdate.getDifficulty());
        }
        if (exerciseUpdate.getUnit() != null) {
            existingExercise.setUnit(exerciseUpdate.getUnit());
        }
        if (exerciseUpdate.getDefaultCaloriesPerUnit() != null) {
            existingExercise.setDefaultCaloriesPerUnit(exerciseUpdate.getDefaultCaloriesPerUnit());
        }

        // Update muscle groups
        if (exerciseUpdate.getMuscleGroups() != null) {
            List<Integer> muscleGroupIds = exerciseUpdate.getMuscleGroups()
                    .stream()
                    .map(MuscleGroup::getId)
                    .toList();
            existingExercise.setMuscleGroups(
                    muscleGroupService.getMuscleGroupByIds(muscleGroupIds)
            );
        }

        // Update image if provided
        MultipartFile file = exerciseUpdate.getImageFile();
        if (file != null && !file.isEmpty()) {
            // Delete old image if exists
            if (existingExercise.getImageId() != null) {
                imageUtils.deleteImage(existingExercise.getImageId());
            }

            // Upload new image
            List<Object> uploadResult = imageUtils.uploadImage(file, ImageUtils.ImageType.EXERCISE);
            existingExercise.setImageId((String) uploadResult.get(0));
            existingExercise.setImageUrl((String) uploadResult.get(1));
        }

        return exerciseRepository.save(existingExercise);
    }

    @PreAuthorize("isAuthenticated()")
    public void deleteExercise(int exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NotFoundException("Exercise not found"));

        // Delete image if exists
        if (exercise.getImageId() != null) {
            try {
                imageUtils.deleteImage(exercise.getImageId());
            } catch (Exception e) {
                // Log error but continue with deletion
                System.err.println("Failed to delete image: " + e.getMessage());
            }
        }

        exerciseRepository.delete(exercise);
    }
}
