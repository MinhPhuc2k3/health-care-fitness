package com.health_fitness.model.statistics.classid;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class MonthlyExerciseStatId implements Serializable {

    private LocalDateTime month;
    private Integer exerciseId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MonthlyExerciseStatId that)) return false;
        return Objects.equals(month, that.month)
                && Objects.equals(exerciseId, that.exerciseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(month, exerciseId);
    }
}
