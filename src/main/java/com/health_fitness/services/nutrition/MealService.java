package com.health_fitness.services.nutrition;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.nutrition.Meal;
import com.health_fitness.repository.nutrition.MealRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MealService {

    private final MealRepository mealRepository;
    private final MenuService menuService;

    @PreAuthorize("isAuthenticated()")
    public Meal getMeal(int mealId){
        return mealRepository.findById(mealId).orElseThrow(()->new NotFoundException("Meal's not found"));
    }

    @PreAuthorize("isAuthenticated()")
    public Meal addMeal(int menuId){
        Meal meal = Meal.builder()
                .menu(menuService.getMenuById(menuId))
                .build();
        return mealRepository.save(meal);
    }

    @PreAuthorize("isAuthenticated()")
    public void deleteMeal(int mealId){
        mealRepository.deleteById(mealId);
    }

    @PreAuthorize("isAuthenticated()")
    public Meal updateMeal(int mealId, Meal meal){
        Meal mealToSave = getMeal(mealId);
        BeanUtils.copyProperties(meal, mealToSave, "id", "menu");
        return mealRepository.save(mealToSave);
    }
}
