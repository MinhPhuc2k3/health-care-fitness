package com.health_fitness.model.workout;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.util.List;

@Entity
@Table(name = "plan_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    @JsonBackReference
    private Plan plan;

    @Column(nullable = false)
    private DayOfWeek sessionDayOfWeek;

    @Column(nullable = false)
    private Float targetCalories;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Exercise.ExerciseCategory category;

    @Column
    @ManyToMany
    private List<MuscleGroup> muscleGroups;

    public enum ExerciseCategory {
        CARDIO, STRENGTH
    }
}
