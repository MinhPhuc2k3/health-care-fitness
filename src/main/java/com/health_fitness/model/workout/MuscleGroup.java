package com.health_fitness.model.workout;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.health_fitness.model.user.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "muscle_groups")
@Entity
public class MuscleGroup extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Override
    public String toString() {
        return "id: "+id+", name: "+name;
    }
}
