package com.health_fitness.controllers.statistics;
import com.health_fitness.model.statistics.MonthlyExerciseStat;
import com.health_fitness.model.statistics.MonthlyRecipeStat;
import com.health_fitness.model.statistics.MonthlyUserStatistics;
import com.health_fitness.services.statistic.DashboardAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardAnalyticsController {

    private final DashboardAnalyticsService service;

    /* ================= USER ================= */

    @GetMapping("/users/monthly")
    public List<MonthlyUserStatistics> getUserStatistics() {
        return service.getUserStatistics();
    }

    /* ================= EXERCISE ================= */

    @GetMapping("/exercises/monthly")
    public List<MonthlyExerciseStat> getExerciseRanking(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return service.getExerciseRanking(year, month);
    }

    /* ================= RECIPE ================= */

    @GetMapping("/recipes/monthly")
    public List<MonthlyRecipeStat> getRecipeRanking(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return service.getRecipeRanking(year, month);
    }
}
