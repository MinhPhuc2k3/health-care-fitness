package com.health_fitness.repository.nutrition;

import com.health_fitness.model.nutrition.MenuPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;

@Repository
public interface MenuPlanRepository extends JpaRepository<MenuPlan, Integer> {
    MenuPlan findByDayOfWeek(DayOfWeek dayOfWeek);
}