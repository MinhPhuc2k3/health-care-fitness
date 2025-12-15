package com.health_fitness.services.nutrition;

import com.health_fitness.model.nutrition.RecipeIngredient;
import com.health_fitness.repository.nutrition.RecipeIngredientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class RecipeIngredientService {
    private final RecipeIngredientRepository recipeIngredientRepository;

    public List<RecipeIngredient> findByIds(List<Integer> recipeIngredientIds){
        return recipeIngredientRepository.findAllById(recipeIngredientIds);
    }
}
