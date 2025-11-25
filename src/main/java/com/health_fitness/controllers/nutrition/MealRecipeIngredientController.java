package com.health_fitness.controllers.nutrition;

import com.health_fitness.model.nutrition.MealRecipeIngredient;
import com.health_fitness.services.nutrition.MealRecipeIngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meal-recipe-ingredients")
@RequiredArgsConstructor
class MealRecipeIngredientController {

    private final MealRecipeIngredientService mealRecipeIngredientService;

    @GetMapping("/{mealRecipeIngredientId}")
    public ResponseEntity<MealRecipeIngredient> getMealRecipeIngredient(
            @PathVariable int mealRecipeIngredientId) {
        MealRecipeIngredient ingredient = mealRecipeIngredientService
                .getMealRecipeIngredient(mealRecipeIngredientId);
        return ResponseEntity.ok(ingredient);
    }

    @PutMapping("/{mealRecipeIngredientId}")
    public ResponseEntity<MealRecipeIngredient> updateMealRecipeIngredient(
            @PathVariable int mealRecipeIngredientId,
            @RequestBody MealRecipeIngredient mealRecipeIngredient) {
        MealRecipeIngredient updated = mealRecipeIngredientService
                .updateMealRecipeIngredient(mealRecipeIngredientId, mealRecipeIngredient);
        return ResponseEntity.ok(updated);
    }
}