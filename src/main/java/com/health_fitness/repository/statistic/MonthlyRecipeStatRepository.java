package com.health_fitness.repository.statistic;

import com.health_fitness.model.statistics.MonthlyRecipeStat;
import com.health_fitness.model.statistics.classid.MonthlyRecipeStatId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MonthlyRecipeStatRepository
        extends JpaRepository<MonthlyRecipeStat, MonthlyRecipeStatId> {

    List<MonthlyRecipeStat> findByMonthOrderByRankAsc(LocalDateTime month);
}