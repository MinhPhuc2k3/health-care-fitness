package com.health_fitness.model.workout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ExerciseBulkImportResponse {
    private int successCount;
    private int failureCount;
    private List<String> errors;
}