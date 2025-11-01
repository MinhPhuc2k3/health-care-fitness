package com.health_fitness.controllers;

import com.health_fitness.model.recipe.meal.Meal;
import com.health_fitness.services.MealService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/meal")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping
    public Meal createMeal(@RequestBody Meal meal){
        return mealService.createMean(meal);
    }

    @GetMapping
    public Page<Meal> getMealByDate(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        return mealService.getMeal(page, size);
    }

    @GetMapping("/{id}")
    public Meal getMealById(@PathVariable Long id){
        return mealService.getMealById(id);
    }

    @PutMapping("/{id}")
    public void updateMeal(@PathVariable Long id, @RequestBody Meal meal){
        mealService.updateMeal(id, meal);
    }

    @DeleteMapping("/{mealId}")
    public void deleteMeal(@PathVariable Long mealId){
        mealService.deleteMeal(mealId);
    }
}
