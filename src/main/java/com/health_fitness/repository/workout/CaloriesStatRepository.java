package com.health_fitness.repository.workout;

import com.health_fitness.model.workout.CaloriesStat;
import com.health_fitness.model.workout.classid.CaloriesStatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CaloriesStatRepository extends JpaRepository<CaloriesStat, CaloriesStatId> {

    @Query("SELECT cs FROM CaloriesStat cs WHERE cs.userId = :userId AND cs.date BETWEEN :startDate AND :endDate")
    List<CaloriesStat> getCaloriesStat(Integer userId, LocalDate startDate, LocalDate endDate);
}
