package com.health_fitness.controllers.nutrition;

import com.health_fitness.model.nutrition.Ingredient;
import com.health_fitness.services.nutrition.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Page<Ingredient>> getAllIngredients(@RequestParam(required = false) String search, Pageable pageable) {
        Page<Ingredient> ingredients = ingredientService.getAllIngredients(search, pageable);
        return ResponseEntity.ok(ingredients);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Integer id) throws IOException {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/excel/template")
    public ResponseEntity<byte[]> downloadExcelTemplate() throws IOException {
        byte[] template = ingredientService.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "ingredient_template.xlsx");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(template);
    }

    @PostMapping(value = "/excel/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importIngredientsFromExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        try {
            List<Ingredient> ingredients = ingredientService.importIngredientsFromExcel(file, images);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Import thành công");
            response.put("totalImported", ingredients.size());
            response.put("totalImages", images != null ? images.size() : 0);
            response.put("data", ingredients);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Có lỗi trong quá trình import");
            errorResponse.put("errors", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);

        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi xử lý file Excel");
            errorResponse.put("errors", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}