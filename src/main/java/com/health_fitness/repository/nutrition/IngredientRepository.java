package com.health_fitness.repository.nutrition;

import com.health_fitness.model.nutrition.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
}
