package com.health_fitness.model.recipe.meal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.health_fitness.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "meal")
@Getter
@Setter
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate createdDate;
    private Float totalCalories;
    private Float totalProtein;
    private Float totalCarbs;
    private Float totalFat;
    private LocalDate mealDate;

    @Enumerated(EnumType.STRING)
    private MealType mealTime;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name = "meal_recipe",
            joinColumns = @JoinColumn(name = "meal_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id")
    )
    private List<Recipe> recipes;
}

