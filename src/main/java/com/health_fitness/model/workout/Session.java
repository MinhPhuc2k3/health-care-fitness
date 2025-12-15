package com.health_fitness.model.workout;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.health_fitness.model.user.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_session_id")
    private PlanSession planSession;

    @Column
    private Float estimatedCalories = 0F;

    @Column
    private Float actualDurationMinutes = 0F;

    @Column
    private Float actualCaloriesBurned = 0F;

    @Column(columnDefinition = "TEXT")
    private String notes = "";

    @Enumerated
    @Column
    private SessionStatus status;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<SessionExercise> sessionExercises = new ArrayList<>();

    private float totalExerciseCalories;

    public enum SessionStatus{
        DONE, IN_PROGRESS
    }

    public void addEstimatedCalories(float delta){
        this.estimatedCalories += delta;
    }
}
