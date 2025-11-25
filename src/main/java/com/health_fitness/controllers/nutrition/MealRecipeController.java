package com.health_fitness.controllers.nutrition;

import com.health_fitness.model.nutrition.MealRecipe;
import com.health_fitness.services.nutrition.MealRecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meal-recipes")
@RequiredArgsConstructor
class MealRecipeController {

    private final MealRecipeService mealRecipeService;

    @PostMapping
    public ResponseEntity<MealRecipe> createMealRecipe(
            @Valid @RequestBody MealRecipe mealRecipe) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mealRecipeService.createMealRecipe(mealRecipe));
    }

    @GetMapping("/{mealRecipeId}")
    public ResponseEntity<MealRecipe> getMealRecipe(@PathVariable int mealRecipeId) {
        MealRecipe mealRecipe = mealRecipeService.getMealRecipe(mealRecipeId);
        return ResponseEntity.ok(mealRecipe);
    }

    @PutMapping("/{mealRecipeId}")
    public ResponseEntity<MealRecipe> updateMealRecipe(
            @PathVariable int mealRecipeId,
            @RequestBody MealRecipe mealRecipe) {
        MealRecipe updated = mealRecipeService.updateMealRecipe(mealRecipeId, mealRecipe);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{mealRecipeId}")
    public ResponseEntity<Void> deleteMealRecipe(@PathVariable int mealRecipeId) {
        mealRecipeService.deleteMealRecipe(mealRecipeId);
        return ResponseEntity.noContent().build();
    }
}