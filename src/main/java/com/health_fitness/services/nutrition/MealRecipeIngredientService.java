package com.health_fitness.services.nutrition;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.nutrition.Ingredient;
import com.health_fitness.model.nutrition.MealRecipe;
import com.health_fitness.model.nutrition.MealRecipeIngredient;
import com.health_fitness.model.nutrition.Menu;
import com.health_fitness.repository.nutrition.MealRecipeIngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MealRecipeIngredientService {

    private final MealRecipeIngredientRepository mealRecipeIngredientRepository;

    @PreAuthorize("isAuthenticated()")
    public MealRecipeIngredient createMealRecipeIngredient(MealRecipe mealRecipe, Ingredient ingredient, float quantity) {
        MealRecipeIngredient mealRecipeIngredient = MealRecipeIngredient.builder()
                .ingredient(ingredient)
                .mealRecipe(mealRecipe)
                .quantity(quantity)
                .build();
        return mealRecipeIngredientRepository.save(mealRecipeIngredient);
    }

    @PreAuthorize("isAuthenticated()")
    public MealRecipeIngredient getMealRecipeIngredient(int mealRecipeIngredientId) {
        return mealRecipeIngredientRepository.findById(mealRecipeIngredientId).orElseThrow(() -> new NotFoundException("Meal Recipe Ingredient's not found"));
    }

    @PreAuthorize("isAuthenticated()")
    public MealRecipeIngredient updateMealRecipeIngredient(int mealRecipeIngredientId, MealRecipeIngredient mealRecipeIngredient){
        MealRecipeIngredient mealRecipeIngredientToSave = getMealRecipeIngredient(mealRecipeIngredientId);
        Menu menu = mealRecipeIngredientToSave.getMealRecipe().getMeal().getMenu();
        Ingredient ingredient = mealRecipeIngredientToSave.getIngredient();
        float delta = mealRecipeIngredient.getQuantity() - mealRecipeIngredientToSave.getQuantity();
        menu.addTotalCarbs(ingredient.getCarbs()*delta);
        menu.addTotalCalories(ingredient.getCalories()*delta);
        menu.addTotalFat(ingredient.getFat()*delta);
        menu.addTotalProtein(ingredient.getProtein()*delta);
        BeanUtils.copyProperties(mealRecipeIngredient, mealRecipeIngredientToSave, "id");
        return mealRecipeIngredientRepository.save(mealRecipeIngredientToSave);
    }

}
