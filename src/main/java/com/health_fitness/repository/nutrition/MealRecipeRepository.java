package com.health_fitness.repository.nutrition;

import com.health_fitness.model.nutrition.MealRecipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRecipeRepository extends JpaRepository<MealRecipe, Integer> {
}
