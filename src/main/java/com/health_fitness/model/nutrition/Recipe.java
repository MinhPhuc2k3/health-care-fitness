package com.health_fitness.model.nutrition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.health_fitness.model.user.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Recipe name is required")
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @PositiveOrZero
    @Column
    private Float calories;

    @PositiveOrZero
    @Column
    private Float protein;

    @PositiveOrZero
    @Column
    private Float carbs;

    @PositiveOrZero
    @Column
    private Float fat;

    @Column
    private String imageUrl;

    @Column
    private String imageId;

    @Transient
    @JsonIgnore
    MultipartFile image;

    @Enumerated(EnumType.STRING)
    @Column
    private RecipeType type;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

    public enum RecipeType {
        MAIN_DISH, DRINK, SNACK, SIDE_DISH
    }
}
