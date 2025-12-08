package com.health_fitness.model.workout;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.health_fitness.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExerciseCategory category;

    @Column
    @ManyToMany
    private List<MuscleGroup> muscleGroups;

    @Column(nullable = false)
    private Float defaultCaloriesPerUnit;

    @Column
    @Enumerated
    private Unit unit;

    private String imageUrl;
    private String imageId;

    @Transient
    @JsonIgnore
    private MultipartFile imageFile;

    public enum ExerciseCategory {
        CARDIO, STRENGTH
    }

    @Enumerated(EnumType.ORDINAL)
    public User.ActivityLevel difficulty;

    public enum Unit {
        MET, REP
    }
}