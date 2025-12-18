package com.health_fitness.repository.statistic;

import com.health_fitness.model.statistics.MonthlyExerciseStat;
import com.health_fitness.model.statistics.classid.MonthlyExerciseStatId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MonthlyExerciseStatRepository
        extends JpaRepository<MonthlyExerciseStat, MonthlyExerciseStatId> {

    List<MonthlyExerciseStat> findByMonthOrderByRankAsc(LocalDateTime month);
}

