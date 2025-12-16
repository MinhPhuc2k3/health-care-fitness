package com.health_fitness.model.workout.classid;

import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaloriesStatId implements Serializable {
    private Integer userId;
    private LocalDate date;
}
