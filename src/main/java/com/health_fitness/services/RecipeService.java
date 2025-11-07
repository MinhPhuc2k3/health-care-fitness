package com.health_fitness.services;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.recipe.meal.Recipe;
import com.health_fitness.model.user.User;
import com.health_fitness.repository.RecipeRepository;
import com.health_fitness.config.security.CustomUserDetails;
import com.health_fitness.utils.ImageUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final ImageUtils imageUtils;
    @Autowired
    RecipeService(RecipeRepository recipeRepository, ImageUtils imageUtils){
        this.recipeRepository = recipeRepository;
        this.imageUtils = imageUtils;
    }

    @PreAuthorize("isAuthenticated()")
    public Page<Recipe> getRecipes(int page, int size){
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return recipeRepository.findAllByUserOrAdmin(userDetails.getUser().getId(), PageRequest.of(page, size));
    }

    @PreAuthorize("isAuthenticated()")
    public Recipe createRecipe(Recipe recipe) {
        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userDetails.getUser();
        recipe.setCreatedBy(user);
        recipe.setCreatedDate(LocalDate.now());
        recipe.setLastUpdate(LocalDate.now());
        if(recipe.getImageFile()!=null){
            try {
                List<Object> imageResponse = imageUtils.uploadImage(recipe.getImageFile(), ImageUtils.ImageType.RECIPE);
                recipe.setImageId((String) imageResponse.get(0));
                recipe.setImageUrl((String) imageResponse.get(1));
            } catch (IOException ex){
                throw new RuntimeException("Can't upload image");
            }
        }
        return recipeRepository.save(recipe);
    }

    @PreAuthorize("#id == principal.user.id")
    public Recipe updateRecipe(Long id, Recipe updatedRecipe) {
        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userDetails.getUser();
        Recipe existingRecipe = recipeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Recipe not found with id: " + id));
        if(updatedRecipe.getImageFile()!=null){
            try {
                List<Object> imageResponse = imageUtils.uploadImage(updatedRecipe.getImageFile(), ImageUtils.ImageType.RECIPE);
                updatedRecipe.setImageId((String) imageResponse.get(0));
                updatedRecipe.setImageUrl((String) imageResponse.get(1));
            } catch (IOException ex){
                throw new RuntimeException("Can't upload image");
            }
        }
        existingRecipe.setName(updatedRecipe.getName());
        existingRecipe.setDescription(updatedRecipe.getDescription());
        existingRecipe.setImageUrl(updatedRecipe.getImageUrl());
        existingRecipe.setCalories(updatedRecipe.getCalories());
        existingRecipe.setProtein(updatedRecipe.getProtein());
        existingRecipe.setCarbs(updatedRecipe.getCarbs());
        existingRecipe.setFat(updatedRecipe.getFat());
        existingRecipe.setMealType(updatedRecipe.getMealType());
        existingRecipe.setLastUpdate(LocalDate.now());
        return recipeRepository.save(existingRecipe);
    }

    @PreAuthorize("#id == principal.user.id")
    public void deleteRecipe(Long id) {
        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User currentUser = userDetails.getUser();

        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Recipe not found with id: " + id));

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getRole().equalsIgnoreCase("ADMIN"));

        if (!recipe.getCreatedBy().getId().equals(currentUser.getId()) && !isAdmin) {
            throw new SecurityException("Bạn không có quyền xóa công thức này");
        }

        recipeRepository.delete(recipe);
    }
}
