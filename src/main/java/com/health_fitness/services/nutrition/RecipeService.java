package com.health_fitness.services.nutrition;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.nutrition.Ingredient;
import com.health_fitness.model.nutrition.Recipe;
import com.health_fitness.model.nutrition.RecipeIngredient;
import com.health_fitness.repository.nutrition.RecipeRepository;
import com.health_fitness.utils.ImageUtils;
import com.health_fitness.utils.ImageUtils.ImageType;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecipeService {

    private final RecipeRepository recipeRepo;
    private final RecipeIngredientService recipeIngredientService;
    private final IngredientService ingredientService;
    private final ImageUtils imageUtils;

    public RecipeService(RecipeRepository recipeRepo, ImageUtils imageUtils, RecipeIngredientService recipeIngredientService, IngredientService ingredientService) {
        this.recipeRepo = recipeRepo;
        this.imageUtils = imageUtils;
        this.recipeIngredientService = recipeIngredientService;
        this.ingredientService = ingredientService;
    }

    @PreAuthorize("isAuthenticated()")
    public Recipe createRecipe(Recipe recipe) throws IOException {
        Recipe recipeToSave = new Recipe();
        MultipartFile file = recipe.getImage();
        if (file != null && !file.isEmpty()) {
            List<Object> uploadResult = imageUtils.uploadImage(file, ImageType.RECIPE);
            recipeToSave.setImageId((String) uploadResult.get(0));
            recipeToSave.setImageUrl((String) uploadResult.get(1));
        }
        recipeToSave.setDescription(recipe.getDescription());
        recipeToSave.setName(recipe.getName());
        recipeToSave.setType(recipe.getType());
        float totalCalories = 0F;
        float totalCarbs = 0F;
        float totalProteins = 0F;
        float totalFats = 0F;
        Map<Integer, Ingredient> ingredients = ingredientService.getIngredientById(recipe.getRecipeIngredients().stream().map(recipeIngredient -> recipeIngredient.getIngredient().getId()).toList())
                .stream().collect(Collectors.toMap(Ingredient::getId, i->i));
        for(RecipeIngredient recipeIngredient: recipe.getRecipeIngredients()){
            recipeToSave.getRecipeIngredients().add(recipeIngredient);
            recipeIngredient.setRecipe(recipeToSave);
            Ingredient ingredient = ingredients.get(recipeIngredient.getIngredient().getId());
            recipeIngredient.setIngredient(ingredient);
            totalCalories += ingredient.getCalories()*recipeIngredient.getQuantity()/100;
            totalFats += ingredient.getFat()*recipeIngredient.getQuantity()/100;
            totalProteins += ingredient.getProtein()*recipeIngredient.getQuantity()/100;
            totalCarbs += ingredient.getCarbs()*recipeIngredient.getQuantity()/100;
        }
        recipeToSave.setCalories(totalCalories);
        recipeToSave.setFat(totalFats);
        recipeToSave.setProtein(totalProteins);
        recipeToSave.setCarbs(totalCarbs);
        return recipeRepo.save(recipeToSave);
    }

    @PreAuthorize("isAuthenticated()")
    public Recipe updateRecipe(int id, Recipe update) throws IOException {
        Recipe recipe = getRecipe(id);
        BeanUtils.copyProperties(update, recipe, "id");
        MultipartFile file = update.getImage();
        if (file != null && !file.isEmpty()) {
            if (recipe.getImageId() != null) {
                imageUtils.deleteImage(recipe.getImageId());
            }
            List<Object> uploadResult = imageUtils.uploadImage(file, ImageType.RECIPE);
            recipe.setImageId((String) uploadResult.get(0));
            recipe.setImageUrl((String) uploadResult.get(1));
        }

        return recipeRepo.save(recipe);
    }

    @PreAuthorize("isAuthenticated()")
    public Recipe getRecipe(int id) {
        return recipeRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Recipe not found"));
    }
    @PreAuthorize("isAuthenticated()")
    public Page<Recipe> getAllRecipes(String name, Pageable pageable) {
        if(name==null) {
            return recipeRepo.findAll(pageable);
        }else return recipeRepo.findRecipeByName(name, pageable);
    }
    @PreAuthorize("isAuthenticated()")
    public void deleteRecipe(int id) throws IOException {
        Recipe recipe = this.getRecipe(id);
        if (recipe.getImageId() != null) {
            imageUtils.deleteImage(recipe.getImageId());
        }
        recipeRepo.deleteById(id);
    }
}
