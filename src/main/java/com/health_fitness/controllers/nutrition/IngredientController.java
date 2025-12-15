package com.health_fitness.controllers.nutrition;

import com.health_fitness.model.nutrition.Ingredient;
import com.health_fitness.services.nutrition.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @PostMapping
    public ResponseEntity<Ingredient> createIngredient(@ModelAttribute Ingredient ingredient) throws IOException {
        Ingredient created = ingredientService.createIngredient(ingredient);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ingredient> updateIngredient(
            @PathVariable Integer id,
            @ModelAttribute Ingredient ingredient) throws IOException {
        Ingredient updated = ingredientService.updateIngredient(id, ingredient);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredient(@PathVariable Integer id) {
        Ingredient ingredient = ingredientService.getIngredient(id);
        return ResponseEntity.ok(ingredient);
    }

    @GetMapping
    public ResponseEntity<Page<Ingredient>> getAllIngredients(Pageable pageable) {
        Page<Ingredient> ingredients = ingredientService.getAllIngredients(pageable);
        return ResponseEntity.ok(ingredients);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Integer id) throws IOException {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
}