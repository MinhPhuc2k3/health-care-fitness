package com.health_fitness.controllers.nutrition;

import com.health_fitness.model.nutrition.Meal;
import com.health_fitness.services.nutrition.MealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
class MealController {

    private final MealService mealService;

    @PostMapping("/menu/{menuId}")
    public ResponseEntity<Meal> addMeal(@PathVariable int menuId) {
        Meal meal = mealService.addMeal(menuId);
        return ResponseEntity.status(HttpStatus.CREATED).body(meal);
    }

    @GetMapping("/{mealId}")
    public ResponseEntity<Meal> getMeal(@PathVariable int mealId) {
        Meal meal = mealService.getMeal(mealId);
        return ResponseEntity.ok(meal);
    }

    @PutMapping("/{mealId}")
    public ResponseEntity<Meal> updateMeal(
            @PathVariable int mealId,
            @RequestBody Meal meal) {
        Meal updated = mealService.updateMeal(mealId, meal);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{mealId}")
    public ResponseEntity<Void> deleteMeal(@PathVariable int mealId) {
        mealService.deleteMeal(mealId);
        return ResponseEntity.noContent().build();
    }
}