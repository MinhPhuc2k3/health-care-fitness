package com.health_fitness.repository.statistic;
import com.health_fitness.model.statistics.MonthlyUserStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface MonthlyUserStatisticsRepository
        extends JpaRepository<MonthlyUserStatistics, LocalDateTime> {
}
