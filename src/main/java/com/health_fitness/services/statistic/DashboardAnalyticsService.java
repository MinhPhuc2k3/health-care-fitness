package com.health_fitness.services.statistic;

import com.health_fitness.model.statistics.MonthlyExerciseStat;
import com.health_fitness.model.statistics.MonthlyRecipeStat;
import com.health_fitness.model.statistics.MonthlyUserStatistics;
import com.health_fitness.repository.statistic.MonthlyExerciseStatRepository;
import com.health_fitness.repository.statistic.MonthlyRecipeStatRepository;
import com.health_fitness.repository.statistic.MonthlyUserStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardAnalyticsService {

    private final MonthlyUserStatisticsRepository userStatisticsRepository;
    private final MonthlyExerciseStatRepository exerciseStatRepository;
    private final MonthlyRecipeStatRepository recipeStatRepository;

    /* ================= USER ================= */

    public List<MonthlyUserStatistics> getUserStatistics() {
        return userStatisticsRepository.findAll();
    }

    /* ================= EXERCISE ================= */

    public List<MonthlyExerciseStat> getExerciseRanking(int year, int month) {
        LocalDateTime date = LocalDate.of(year, month, 1).atStartOfDay();
        return exerciseStatRepository.findByMonthOrderByRankAsc(date);
    }

    /* ================= RECIPE ================= */

    public List<MonthlyRecipeStat> getRecipeRanking(int year, int month) {
        LocalDateTime date = LocalDate.of(year, month, 1).atStartOfDay();
        return recipeStatRepository.findByMonthOrderByRankAsc(date);
    }
}
