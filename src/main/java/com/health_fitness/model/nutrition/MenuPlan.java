package com.health_fitness.model.nutrition;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.health_fitness.model.user.UserPlan;
import com.health_fitness.model.workout.Plan;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "user plan is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_plan_id", nullable = false)
    @JsonBackReference
    private UserPlan userPlan;

    @NotNull
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @PositiveOrZero
    @Column
    private Float targetCalories;

    @PositiveOrZero
    @Column
    private Float targetProtein;

    @PositiveOrZero
    @Column
    private Float targetCarb;

    @PositiveOrZero
    @Column
    private Float targetFat;

}
