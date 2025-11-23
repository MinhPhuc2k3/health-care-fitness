package com.health_fitness.model.workout;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_session_id", nullable = false)
    private PlanSession planSession;

    @Column
    private Integer actualDurationMinutes;

    @Column
    private Float actualCaloriesBurned;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated
    @Column(nullable = false)
    private SessionStatus status;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<SessionExercise> sessionExercises = new ArrayList<>();

    private float totalExerciseCalories;

    public enum SessionStatus{
        DONE, IN_PROGRESS
    }
}
