package com.health_fitness.services.nutrition;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.meal.MenuPlan;
import com.health_fitness.repository.nutrition.MenuPlanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class MenuPlanService {

    private final MenuPlanRepository menuPlanRepository;

    @PreAuthorize("isAuthenticated()")
    public MenuPlan getMenuPlanToday(){
        return menuPlanRepository.findByDayOfWeek(LocalDate.now().getDayOfWeek());
    }

    @PreAuthorize("isAuthenticated()")
    public MenuPlan getMenuPlan(int menuPlanId){
        return menuPlanRepository.findById(menuPlanId).orElseThrow(()->new NotFoundException("Menu Plan's not found"));
    }
}
