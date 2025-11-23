package com.health_fitness.repository.nutrition;

import com.health_fitness.model.meal.MealRecipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRecipeRepository extends JpaRepository<MealRecipe, Integer> {
}
