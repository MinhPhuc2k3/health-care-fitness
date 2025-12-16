package com.health_fitness.services.workout;

import com.health_fitness.model.workout.CaloriesStat;
import com.health_fitness.repository.workout.CaloriesStatRepository;
import com.health_fitness.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CaloriesStatService {

    private final UserService userService;
    private final CaloriesStatRepository caloriesStatRepository;

    @PreAuthorize("isAuthenticated()")
    public List<CaloriesStat> getCaloriesStatByDate(LocalDate startDate, LocalDate endDate) {
        return caloriesStatRepository.getCaloriesStat(userService.getUser().getId(), startDate, endDate);
    }
}
