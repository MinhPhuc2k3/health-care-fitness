package com.health_fitness.model.user;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.health_fitness.model.workout.Plan;
import lombok.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GoalName name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<Plan> plans = new ArrayList<>();

    public enum GoalName {
        LOSS_WEIGHT, BUILD_MUSCLE, MAINTAIN_WEIGHT
    }
}