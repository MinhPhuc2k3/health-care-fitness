package com.health_fitness.model.workout;

import com.health_fitness.model.workout.classid.CaloriesStatId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@IdClass(CaloriesStatId.class)
public class CaloriesStat{

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Id
    @Column(name = "date")
    private LocalDate date;

    @Column(name = "burned_calories")
    private float burnedCalories;

    @Column(name = "eaten_calories")
    private float eatenCalories;

    @Column(name = "eaten_carbs")
    private float eatenCarbs;

    @Column(name = "eaten_fats")
    private float eatenFats;

    @Column(name = "eaten_proteins")
    private float eatenProteins;
}
