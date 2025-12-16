package com.health_fitness.controllers.workout;

import com.health_fitness.model.workout.CaloriesStat;
import com.health_fitness.services.workout.CaloriesStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/calories-stat")
@RequiredArgsConstructor
public class CaloriesStatController {

    private final CaloriesStatService caloriesStatService;

    @GetMapping
    public List<CaloriesStat> getCaloriesStat(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate){
        return this.caloriesStatService.getCaloriesStatByDate(startDate, endDate);
    }
}
