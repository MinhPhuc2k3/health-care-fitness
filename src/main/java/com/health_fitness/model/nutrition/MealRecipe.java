package com.health_fitness.model.nutrition;

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
    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @NotNull(message = "Meal is required")
    @ManyToOne
    @JoinColumn(name = "meal_id")
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

    public void addCalories(float delta){
        if(this.calories==null) this.calories = 0F;
        this.calories += delta;
    }

    public void addCarbs(float delta){
        if(this.carbs==null) this.carbs = 0F;
        this.carbs += delta;
    }

    public void addProtein(float delta){
        if(this.protein==null) this.protein = 0F;
        this.protein += delta;
    }

    public void addFat(float delta){
        if(this.fat==null) this.fat = 0F;
        this.fat += delta;
    }
}
