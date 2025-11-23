package com.health_fitness.model.workout;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.health_fitness.model.user.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "session_exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionExercise extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exercise_id", nullable = false)
    @NotNull
    private Exercise exercise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @NotNull
    @JsonBackReference
    private Session session;

    @Column(nullable = false)
    private Integer reps;

    @Column(nullable = false)
    private Integer sets;

    @Column(nullable = false)
    private Float weightUsed;
}
