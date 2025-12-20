package com.health_fitness.services.nutrition;

import com.health_fitness.model.nutrition.*;
import com.health_fitness.repository.nutrition.MealRepository;
import com.health_fitness.repository.nutrition.MenuRepository;
import com.health_fitness.repository.nutrition.RecipeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service


@Transactional
@RequiredArgsConstructor
public class MenuAutoFillService {

    private final RecipeRepository recipeRepository;
    private final MenuRepository menuRepository;
    private final MealRepository mealRepository;

    // Phân phối calories cho từng bữa ăn
    private static final Map<Meal.MealType, Float> MEAL_CALORIE_DISTRIBUTION = Map.of(
            Meal.MealType.BREAKFAST, 0.30f,  // 30% calories
            Meal.MealType.LUNCH, 0.35f,      // 35% calories
            Meal.MealType.DINNER, 0.30f,     // 30% calories
            Meal.MealType.SNACK, 0.05f       // 5% calories
    );

    // Ngưỡng để coi như bữa ăn đã đủ (90% target)
    private static final float MEAL_COMPLETE_THRESHOLD = 0.9f;

    public Menu autoFillMenu(Integer menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found"));

        MenuPlan menuPlan = menu.getMenuPlan();
        if (menuPlan == null) {
            throw new RuntimeException("Menu must have a MenuPlan");
        }

        // Lấy tất cả recipes có sẵn
        List<Recipe> allRecipes = recipeRepository.findAll();

        // Tính target cho từng bữa ăn dựa trên menu plan
        MacroTarget menuTarget = new MacroTarget(
                menuPlan.getTargetCalories(),
                menuPlan.getTargetProtein(),
                menuPlan.getTargetCarb(),
                menuPlan.getTargetFat()
        );

        // Phân tích trạng thái của từng bữa ăn
        Map<Meal.MealType, MealStatus> mealStatusMap = analyzeMealStatus(menu, menuTarget);

        // Xử lý từng bữa ăn
        for (Meal.MealType mealType : Meal.MealType.values()) {
            MealStatus status = mealStatusMap.get(mealType);
            MacroTarget mealTarget = calculateMealTarget(mealType, menuTarget);

            if (status.meal == null) {
                // Trường hợp 1: Bữa ăn chưa tồn tại - tạo mới
                Meal meal = createOptimalMeal(mealType, mealTarget, allRecipes);
                meal.setMenu(menu);
                menu.getMeals().add(meal);
                updateMenuTotals(menu, meal);
            } else if (!status.isComplete) {
                // Trường hợp 2: Bữa ăn đã có nhưng chưa đủ target - thêm recipe
                MacroTarget remainingTarget = new MacroTarget(
                        Math.max(0, mealTarget.calories - status.currentMacros.calories),
                        Math.max(0, mealTarget.protein - status.currentMacros.protein),
                        Math.max(0, mealTarget.carbs - status.currentMacros.carbs),
                        Math.max(0, mealTarget.fat - status.currentMacros.fat)
                );

                addRecipesToMeal(status.meal, remainingTarget, allRecipes, menu);
            }
            // Trường hợp 3: Bữa ăn đã đủ - không làm gì
        }

        return menuRepository.save(menu);
    }

    private Map<Meal.MealType, MealStatus> analyzeMealStatus(Menu menu, MacroTarget menuTarget) {
        Map<Meal.MealType, MealStatus> statusMap = new HashMap<>();

        // Khởi tạo tất cả meal types
        for (Meal.MealType type : Meal.MealType.values()) {
            statusMap.put(type, new MealStatus(null, new MacroTarget(0, 0, 0, 0), false));
        }

        // Phân tích các meal hiện có
        for (Meal meal : menu.getMeals()) {
            if (meal.getMealType() == null) continue;

            MacroTarget currentMacros = calculateMealMacros(meal);
            MacroTarget targetMacros = calculateMealTarget(meal.getMealType(), menuTarget);

            // Kiểm tra xem meal đã đủ target chưa (>= 90% calories target)
            boolean isComplete = currentMacros.calories >= (targetMacros.calories * MEAL_COMPLETE_THRESHOLD);

            statusMap.put(meal.getMealType(), new MealStatus(meal, currentMacros, isComplete));
        }

        return statusMap;
    }

    private MacroTarget calculateMealMacros(Meal meal) {
        float calories = 0, protein = 0, carbs = 0, fat = 0;

        for (MealRecipe mr : meal.getMealRecipe()) {
            calories += mr.getCalories() != null ? mr.getCalories() : 0;
            protein += mr.getProtein() != null ? mr.getProtein() : 0;
            carbs += mr.getCarbs() != null ? mr.getCarbs() : 0;
            fat += mr.getFat() != null ? mr.getFat() : 0;
        }

        return new MacroTarget(calories, protein, carbs, fat);
    }

    private MacroTarget calculateMealTarget(Meal.MealType mealType, MacroTarget menuTarget) {
        Float distribution = MEAL_CALORIE_DISTRIBUTION.get(mealType);

        return new MacroTarget(
                menuTarget.calories * distribution,
                menuTarget.protein * distribution,
                menuTarget.carbs * distribution,
                menuTarget.fat * distribution
        );
    }

    private void addRecipesToMeal(Meal meal, MacroTarget remainingTarget, List<Recipe> allRecipes, Menu menu) {
        // Filter recipes phù hợp với meal type
        List<Recipe> suitableRecipes = filterRecipesByMealType(allRecipes, meal.getMealType());

        // Loại bỏ các recipes đã có trong meal
        Set<Integer> existingRecipeIds = meal.getMealRecipe().stream()
                .map(mr -> mr.getRecipe().getId())
                .collect(Collectors.toSet());

        suitableRecipes = suitableRecipes.stream()
                .filter(r -> !existingRecipeIds.contains(r.getId()))
                .collect(Collectors.toList());

        // Tìm recipes để bổ sung
        List<Recipe> additionalRecipes = findOptimalRecipeCombination(suitableRecipes, remainingTarget);

        // Thêm recipes vào meal
        for (Recipe recipe : additionalRecipes) {
            MealRecipe mealRecipe = MealRecipe.builder()
                    .recipe(recipe)
                    .meal(meal)
                    .calories(recipe.getCalories())
                    .protein(recipe.getProtein())
                    .carbs(recipe.getCarbs())
                    .fat(recipe.getFat())
                    .mealRecipeIngredients(new ArrayList<>())
                    .build();

            // Copy ingredients từ recipe
            for (RecipeIngredient ri : recipe.getRecipeIngredients()) {
                MealRecipeIngredient mri = MealRecipeIngredient.builder()
                        .ingredient(ri.getIngredient())
                        .mealRecipe(mealRecipe)
                        .quantity(ri.getQuantity())
                        .build();
                mealRecipe.getMealRecipeIngredients().add(mri);
            }

            meal.getMealRecipe().add(mealRecipe);

            // Cập nhật menu totals
            menu.addTotalCalories(mealRecipe.getCalories() != null ? mealRecipe.getCalories() : 0);
            menu.addTotalProtein(mealRecipe.getProtein() != null ? mealRecipe.getProtein() : 0);
            menu.addTotalCarbs(mealRecipe.getCarbs() != null ? mealRecipe.getCarbs() : 0);
            menu.addTotalFat(mealRecipe.getFat() != null ? mealRecipe.getFat() : 0);
        }
    }

    private Meal createOptimalMeal(Meal.MealType mealType, MacroTarget target, List<Recipe> allRecipes) {
        // Filter recipes phù hợp với meal type
        List<Recipe> suitableRecipes = filterRecipesByMealType(allRecipes, mealType);

        // Tìm combination tốt nhất
        List<Recipe> selectedRecipes = findOptimalRecipeCombination(suitableRecipes, target);

        Meal meal = Meal.builder()
                .mealType(mealType)
                .mealRecipe(new ArrayList<>())
                .build();

        // Tạo MealRecipe cho mỗi recipe được chọn
        for (Recipe recipe : selectedRecipes) {
            MealRecipe mealRecipe = MealRecipe.builder()
                    .recipe(recipe)
                    .meal(meal)
                    .calories(recipe.getCalories())
                    .protein(recipe.getProtein())
                    .carbs(recipe.getCarbs())
                    .fat(recipe.getFat())
                    .mealRecipeIngredients(new ArrayList<>())
                    .build();

            // Copy ingredients từ recipe
            for (RecipeIngredient ri : recipe.getRecipeIngredients()) {
                MealRecipeIngredient mri = MealRecipeIngredient.builder()
                        .ingredient(ri.getIngredient())
                        .mealRecipe(mealRecipe)
                        .quantity(ri.getQuantity())
                        .build();
                mealRecipe.getMealRecipeIngredients().add(mri);
            }

            meal.getMealRecipe().add(mealRecipe);
        }

        return meal;
    }

    private List<Recipe> filterRecipesByMealType(List<Recipe> recipes, Meal.MealType mealType) {
        return recipes.stream()
                .filter(r -> isRecipeSuitableForMealType(r, mealType))
                .collect(Collectors.toList());
    }

    private boolean isRecipeSuitableForMealType(Recipe recipe, Meal.MealType mealType) {
        if (recipe.getType() == null) return true;

        switch (mealType) {
            case BREAKFAST:
                return recipe.getType() == Recipe.RecipeType.MAIN_DISH ||
                        recipe.getType() == Recipe.RecipeType.DRINK;
            case LUNCH:
            case DINNER:
                return recipe.getType() == Recipe.RecipeType.MAIN_DISH ||
                        recipe.getType() == Recipe.RecipeType.SIDE_DISH;
            case SNACK:
                return recipe.getType() == Recipe.RecipeType.SNACK ||
                        recipe.getType() == Recipe.RecipeType.DRINK;
            default:
                return true;
        }
    }

    private List<Recipe> findOptimalRecipeCombination(List<Recipe> recipes, MacroTarget target) {
        if (recipes.isEmpty()) return new ArrayList<>();

        List<Recipe> bestCombination = new ArrayList<>();
        double bestScore = Double.MAX_VALUE;

        for (int size = 1; size <= Math.min(3, recipes.size()); size++) {
            List<List<Recipe>> combinations = generateCombinations(recipes, size);

            for (List<Recipe> combination : combinations) {
                double score = calculateMacroScore(combination, target);
                if (score < bestScore) {
                    bestScore = score;
                    bestCombination = new ArrayList<>(combination);
                }
            }
        }

        if (bestCombination.isEmpty() && !recipes.isEmpty()) {
            Recipe closest = recipes.stream()
                    .min((r1, r2) -> Double.compare(
                            calculateMacroScore(List.of(r1), target),
                            calculateMacroScore(List.of(r2), target)
                    ))
                    .orElse(recipes.get(0));
            bestCombination.add(closest);
        }

        return bestCombination;
    }

    private double calculateMacroScore(List<Recipe> recipes, MacroTarget target) {
        float totalCalories = 0, totalProtein = 0, totalCarbs = 0, totalFat = 0;

        for (Recipe recipe : recipes) {
            totalCalories += recipe.getCalories() != null ? recipe.getCalories() : 0;
            totalProtein += recipe.getProtein() != null ? recipe.getProtein() : 0;
            totalCarbs += recipe.getCarbs() != null ? recipe.getCarbs() : 0;
            totalFat += recipe.getFat() != null ? recipe.getFat() : 0;
        }

        // Tránh chia cho 0
        float calorieTarget = target.calories > 0 ? target.calories : 1;
        float proteinTarget = target.protein > 0 ? target.protein : 1;
        float carbsTarget = target.carbs > 0 ? target.carbs : 1;
        float fatTarget = target.fat > 0 ? target.fat : 1;

        // Tính sai số bình phương có trọng số
        double calorieError = Math.pow((totalCalories - target.calories) / calorieTarget, 2) * 2.0;
        double proteinError = Math.pow((totalProtein - target.protein) / proteinTarget, 2) * 1.5;
        double carbsError = Math.pow((totalCarbs - target.carbs) / carbsTarget, 2) * 1.0;
        double fatError = Math.pow((totalFat - target.fat) / fatTarget, 2) * 1.0;

        return calorieError + proteinError + carbsError + fatError;
    }

    private List<List<Recipe>> generateCombinations(List<Recipe> recipes, int size) {
        List<List<Recipe>> result = new ArrayList<>();
        generateCombinationsHelper(recipes, size, 0, new ArrayList<>(), result);
        return result;
    }

    private void generateCombinationsHelper(List<Recipe> recipes, int size, int start,
                                            List<Recipe> current, List<List<Recipe>> result) {
        if (current.size() == size) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < recipes.size(); i++) {
            current.add(recipes.get(i));
            generateCombinationsHelper(recipes, size, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    private void updateMenuTotals(Menu menu, Meal meal) {
        for (MealRecipe mr : meal.getMealRecipe()) {
            menu.addTotalCalories(mr.getCalories() != null ? mr.getCalories() : 0);
            menu.addTotalProtein(mr.getProtein() != null ? mr.getProtein() : 0);
            menu.addTotalCarbs(mr.getCarbs() != null ? mr.getCarbs() : 0);
            menu.addTotalFat(mr.getFat() != null ? mr.getFat() : 0);
        }
    }

    private static class MacroTarget {
        float calories;
        float protein;
        float carbs;
        float fat;

        MacroTarget(float calories, float protein, float carbs, float fat) {
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fat = fat;
        }
    }

    private static class MealStatus {
        Meal meal;
        MacroTarget currentMacros;
        boolean isComplete;

        MealStatus(Meal meal, MacroTarget currentMacros, boolean isComplete) {
            this.meal = meal;
            this.currentMacros = currentMacros;
            this.isComplete = isComplete;
        }
    }
}