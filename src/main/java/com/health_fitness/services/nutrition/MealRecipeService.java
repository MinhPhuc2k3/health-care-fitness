package com.health_fitness.services.nutrition;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.nutrition.Meal;
import com.health_fitness.model.nutrition.MealRecipe;
import com.health_fitness.model.nutrition.Recipe;
import com.health_fitness.repository.nutrition.MealRecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MealRecipeService {

    private final MealRecipeRepository mealRecipeRepository;
    private final RecipeService recipeService;
    private final MealService mealService;
    private final MealRecipeIngredientService mealRecipeIngredientService;

    @PreAuthorize("isAuthenticated()")
    public MealRecipe getMealRecipe(int mealRecipeId){
        return mealRecipeRepository.findById(mealRecipeId).orElseThrow(()->new NotFoundException("Meal recipe not found"));
    }

    @PreAuthorize("isAuthenticated()")
    public MealRecipe createMealRecipe(MealRecipe mealRecipeRequest){
        Recipe recipe = recipeService.getRecipe(mealRecipeRequest.getRecipe().getId());
        Meal meal = mealService.getMeal(mealRecipeRequest.getMeal().getId());
        MealRecipe mealRecipe = MealRecipe.builder()
                .recipe(recipe)
                .meal(meal)
                .calories(recipe.getCalories())
                .fat(recipe.getFat())
                .carbs(recipe.getCarbs())
                .protein(recipe.getProtein())
                .build();
        meal.getMenu().addTotalCalories(recipe.getCalories());
        meal.getMenu().addTotalFat(recipe.getFat());
        meal.getMenu().addTotalCarbs(recipe.getCarbs());
        meal.getMenu().addTotalProtein(recipe.getProtein());
        MealRecipe savedMealRecipe = mealRecipeRepository.save(mealRecipe);
        savedMealRecipe.getRecipe().getRecipeIngredients().forEach((recipeIngredient)->{
            mealRecipeIngredientService.createMealRecipeIngredient(mealRecipe, recipeIngredient.getIngredient(), recipeIngredient.getQuantity());
        });
        return savedMealRecipe;
    }

    @PreAuthorize("isAuthenticated()")
    public MealRecipe updateMealRecipe(int mealRecipeId, MealRecipe mealRecipe){
        MealRecipe savedMealRecipe = getMealRecipe(mealRecipeId);
        BeanUtils.copyProperties(mealRecipe, savedMealRecipe, "id");
        return savedMealRecipe;
    }

    @PreAuthorize("isAuthenticated()")
    public void deleteMealRecipe(int mealRecipeId){
        mealRecipeRepository.findById(mealRecipeId);
    }
}
