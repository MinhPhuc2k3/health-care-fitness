package com.health_fitness.model.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.health_fitness.model.nutrition.MenuPlan;
import com.health_fitness.model.workout.Plan;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPlan extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column
    private PlanStatus status;

    public enum PlanStatus {
        ACTIVE, COMPLETED, GIVEN_UP
    }

    @OneToMany(mappedBy = "userPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<MenuPlan> menuPlans = new ArrayList<>();
}