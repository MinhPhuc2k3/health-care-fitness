package com.health_fitness.model.meal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @NotNull(message = "Plan is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    @JsonBackReference
    private Plan plan;

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

    @OneToMany(mappedBy = "menuPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Menu> menus = new ArrayList<>();

}
