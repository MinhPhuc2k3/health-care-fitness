package com.health_fitness.model.statistics.classid;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class MonthlyRecipeStatId implements Serializable {

    private LocalDateTime month;
    private Integer recipeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MonthlyRecipeStatId that)) return false;
        return Objects.equals(month, that.month)
                && Objects.equals(recipeId, that.recipeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(month, recipeId);
    }
}
