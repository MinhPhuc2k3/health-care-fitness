package com.health_fitness.services.nutrition;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.nutrition.Menu;
import com.health_fitness.model.nutrition.MenuPlan;
import com.health_fitness.model.user.UserPlan;
import com.health_fitness.repository.nutrition.MenuPlanRepository;
import com.health_fitness.services.user.UserService;
import com.health_fitness.services.workout.UserPlanService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MenuPlanService {

    private final MenuPlanRepository menuPlanRepository;
    private final UserPlanService userPlanService;

    @PreAuthorize("isAuthenticated()")
    public MenuPlan getMenuPlanToday(){
        List<MenuPlan> menuPlans = userPlanService.getActiveUserPlan().getMenuPlans();
        for(MenuPlan menuPlan: menuPlans) {
            if(menuPlan.getDayOfWeek().equals(LocalDate.now().getDayOfWeek())){
                return menuPlan;
            }
        }
        throw  new NotFoundException("Not found menu plan please add user plan");
    }

    @PreAuthorize("isAuthenticated()")
    public MenuPlan getMenuPlan(int menuPlanId){
        return menuPlanRepository.findById(menuPlanId).orElseThrow(()->new NotFoundException("Menu Plan's not found"));
    }
}
