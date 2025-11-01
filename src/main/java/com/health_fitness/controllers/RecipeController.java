package com.health_fitness.controllers;

import com.health_fitness.model.recipe.meal.Recipe;
import com.health_fitness.services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/recipe")
public class RecipeController {

    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService){
        this.recipeService = recipeService;
    }

    @GetMapping
    public Page<Recipe> getListRecipe(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        return recipeService.getRecipes(page, size);
    }

    @PostMapping()
    public Recipe createRecipe(@ModelAttribute Recipe recipe) {
        return recipeService.createRecipe(recipe);
    }

    @PutMapping("/{id}")
    public Recipe updateRecipe(@PathVariable Long id, @ModelAttribute Recipe recipe) {
        return recipeService.updateRecipe(id, recipe);
    }

    @DeleteMapping("/{id}")
    public void deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
    }
}
