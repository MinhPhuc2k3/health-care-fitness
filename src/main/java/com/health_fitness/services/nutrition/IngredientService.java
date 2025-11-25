package com.health_fitness.services.nutrition;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.nutrition.Ingredient;
import com.health_fitness.repository.nutrition.IngredientRepository;
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
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final ImageUtils imageUtils;


    public IngredientService(IngredientRepository ingredientRepo, ImageUtils imageUtils) {
        this.ingredientRepository = ingredientRepo;
        this.imageUtils = imageUtils;
    }

    @PreAuthorize("isAuthenticated()")
    public Ingredient createIngredient(Ingredient ingredient) throws IOException {
        MultipartFile file = ingredient.getImage();
        if (file != null && !file.isEmpty()) {
            List<Object> uploadResult = imageUtils.uploadImage(file, ImageType.INGREDIENT);
            ingredient.setImageId((String) uploadResult.get(0));
            ingredient.setImageUrl((String) uploadResult.get(1));
        }
        return ingredientRepository.save(ingredient);
    }

    @PreAuthorize("isAuthenticated()")
    public Ingredient updateIngredient(Long id, Ingredient ingredient) throws IOException {
        Ingredient ingredientToSave = getIngredient(id);
        BeanUtils.copyProperties(ingredient, ingredientToSave, "id");
        MultipartFile file = ingredient.getImage();
        if (file != null && !file.isEmpty()) {
            if (ingredient.getImageId() != null) {
                imageUtils.deleteImage(ingredient.getImageId());
            }
            List<Object> uploadResult = imageUtils.uploadImage(file, ImageType.INGREDIENT);
            ingredient.setImageId((String) uploadResult.get(0));
            ingredient.setImageUrl((String) uploadResult.get(1));
        }

        return ingredientRepository.save(ingredient);
    }

    @PreAuthorize("isAuthenticated()")
    public Ingredient getIngredient(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));
    }

    public Page<Ingredient> getAllIngredients(Pageable pageable) {
        return ingredientRepository.findAll(pageable);
    }

    @PreAuthorize("isAuthenticated()")
    public void deleteIngredient(Long id) throws IOException {
        Ingredient ingredient = this.getIngredient(id);
        if (ingredient.getImageId() != null) {
            imageUtils.deleteImage(ingredient.getImageId());
        }
        ingredientRepository.deleteById(id);
    }
}
