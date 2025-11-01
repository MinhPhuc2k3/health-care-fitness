package com.health_fitness.services;

import com.health_fitness.exception.BadRequestException;
import com.health_fitness.exception.NotFoundException;
import com.health_fitness.exception.UpdateMealBeforeCurrentDateException;
import com.health_fitness.model.recipe.meal.Meal;
import com.health_fitness.model.recipe.meal.Recipe;
import com.health_fitness.repository.MealRepository;
import com.health_fitness.repository.RecipeRepository;
import com.health_fitness.config.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MealService {

    private final MealRepository mealRepository;
    private  final RecipeRepository recipeRepository;
    @Autowired
    public MealService(MealRepository mealRepository, RecipeRepository recipeRepository) {
        this.mealRepository = mealRepository;
        this.recipeRepository = recipeRepository;
    }

    @PreAuthorize("isAuthenticated()")
    public Page<Meal> getMeal(int page, int size){
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return mealRepository.findAll(PageRequest.of(page, size), userDetails.getUser().getId());
    }

    private void validateMealBeforeSave(Meal meal){
        if(meal.getMealDate() == null) throw new BadRequestException("Please add meal date!");
        if(meal.getMealDate().isBefore(LocalDate.now())) throw new BadRequestException("Meal date must not before today");
        if(meal.getRecipes().isEmpty()) throw new BadRequestException("Please add recipe!");
        if(meal.getMealTime() == null) throw  new BadRequestException("Please add meal time");
    }

    @PreAuthorize("isAuthenticated()")
    public Meal createMean(Meal meal) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        meal.setCreatedBy(userDetails.getUser());
        meal.setCreatedDate(LocalDate.now());
        List<Recipe> recipes = (List<Recipe>) recipeRepository.findAllById(meal.getRecipes().stream().map(Recipe::getId).collect(Collectors.toSet()));
        meal.setRecipes(recipes);
        validateMealBeforeSave(meal);
        float totalCalories = 0, totalProtein = 0, totalCarbs = 0, totalFat = 0;
        for (Recipe r :meal.getRecipes()) {
            totalCalories += r.getCalories();
            totalProtein += r.getProtein();
            totalCarbs += r.getCarbs();
            totalFat += r.getFat();
        }
        meal.setTotalCalories(totalCalories);
        meal.setTotalProtein(totalProtein);
        meal.setTotalCarbs(totalCarbs);
        meal.setTotalFat(totalFat);
        return mealRepository.save(meal);
    }

    @PreAuthorize("#id == principal.user.id")
    public void updateMeal(Long id, Meal meal){
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Meal currentMeal = this.mealRepository.findById(id).orElseThrow(() ->new NotFoundException("Meal not found"));
        if(!currentMeal.getCreatedBy().getId().equals(userDetails.getUser().getId())){
            throw new SecurityException("You don't have permission access this resource");
        }
        List<Recipe> recipes = (List<Recipe>) recipeRepository.findAllById(meal.getRecipes().stream().map(Recipe::getId).collect(Collectors.toSet()));
        meal.setRecipes(recipes);
        validateMealBeforeSave(meal);
        float totalCalories = 0, totalProtein = 0, totalCarbs = 0, totalFat = 0;
        for (Recipe r :meal.getRecipes()) {
            totalCalories += r.getCalories();
            totalProtein += r.getProtein();
            totalCarbs += r.getCarbs();
            totalFat += r.getFat();
        }
        currentMeal.setMealTime(meal.getMealTime());
        currentMeal.setMealDate(meal.getMealDate());
        currentMeal.setTotalCalories(totalCalories);
        currentMeal.setTotalProtein(totalProtein);
        currentMeal.setTotalCarbs(totalCarbs);
        currentMeal.setTotalFat(totalFat);
        mealRepository.save(currentMeal);
    }

    @PreAuthorize("#id == principal.user.id")
    public  void deleteMeal(Long mealId){
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Meal meal = this.mealRepository.findById(mealId).orElseThrow(() ->new NotFoundException("Meal not found"));
        if(!meal.getCreatedBy().getId().equals(userDetails.getUser().getId())){
            throw new SecurityException("You don't have permission access this resource");
        }
        if(meal.getCreatedDate() == null){
            throw  new BadRequestException("created date is null");
        }
        if(meal.getCreatedDate().isBefore(LocalDate.now())){
            throw new UpdateMealBeforeCurrentDateException(meal.getCreatedDate());
        }
        mealRepository.delete(meal);
    }

    @PostAuthorize("returnObject.createdBy.id == principal.user.id")
    public Meal getMealById(Long mealId) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return mealRepository.findById(mealId).orElseThrow(() -> new NotFoundException("Meal not found"));
    }
}
