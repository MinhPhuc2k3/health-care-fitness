package com.health_fitness.model.statistics;

import com.health_fitness.model.statistics.classid.MonthlyRecipeStatId;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_recipe_stat")
@Getter
@Immutable
@IdClass(MonthlyRecipeStatId.class)
public class MonthlyRecipeStat {

    @Id
    private LocalDateTime month;

    @Id
    @Column(name = "recipe_id")
    private Integer recipeId;

    private String name;

    private String image_url;

    @Column(name = "total_times")
    private Long totalTimes;

    private Integer rank;
}
