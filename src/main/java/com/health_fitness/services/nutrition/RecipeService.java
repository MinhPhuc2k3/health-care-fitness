package com.health_fitness.services.nutrition;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.nutrition.Recipe;
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

@Service
@Transactional
public class RecipeService {

    private final RecipeRepository recipeRepo;
    private final ImageUtils imageUtils;

    public RecipeService(RecipeRepository recipeRepo, ImageUtils imageUtils) {
        this.recipeRepo = recipeRepo;
        this.imageUtils = imageUtils;
    }

    @PreAuthorize("isAuthenticated()")
    public Recipe createRecipe(Recipe recipe) throws IOException {
        MultipartFile file = recipe.getImage();
        if (file != null && !file.isEmpty()) {
            List<Object> uploadResult = imageUtils.uploadImage(file, ImageType.RECIPE);
            recipe.setImageId((String) uploadResult.get(0));
            recipe.setImageUrl((String) uploadResult.get(1));
        }
        return recipeRepo.save(recipe);
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
    public Page<Recipe> getAllRecipes(Pageable pageable) {
        return recipeRepo.findAll(pageable);
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
