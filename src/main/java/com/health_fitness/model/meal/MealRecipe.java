package com.health_fitness.model.meal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "meal_recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Recipe is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @NotNull(message = "Meal is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    @JsonBackReference
    private Meal meal;

    @OneToMany(mappedBy = "mealRecipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<MealRecipeIngredient> mealRecipeIngredients = new ArrayList<>();

    @PositiveOrZero
    @Column
    private Float fat;

    @PositiveOrZero
    @Column
    private Float calories;

    @PositiveOrZero
    @Column
    private Float carbs;

    @PositiveOrZero
    @Column
    private Float protein;
}
