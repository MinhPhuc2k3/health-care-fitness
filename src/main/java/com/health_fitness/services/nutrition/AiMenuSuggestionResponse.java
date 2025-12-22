package com.health_fitness.services.nutrition;

import lombok.Data;

import java.util.List;

@Data
public class AiMenuSuggestionResponse {
    private String notes;
    private Float totalCalories;
    private Float totalProtein;
    private Float totalCarbs;
    private Float totalFat;
    private List<AiMealDTO> meals;
}

@Data
class AiMealDTO {
    private String mealType; // BREAKFAST, LUNCH...
    private List<AiMealRecipeDTO> recipes;
}

@Data
class AiMealRecipeDTO {
    private Integer recipeId;
    private Float quantity; // Giả định quantity dùng để tính toán macro
    private List<AiIngredientDTO> ingredients;
}

@Data
class AiIngredientDTO {
    private Integer ingredientId;
    private Float quantity;
}