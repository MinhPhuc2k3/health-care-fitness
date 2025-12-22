package com.health_fitness.services.nutrition;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.nutrition.Menu;
import com.health_fitness.model.nutrition.MenuPlan;
import com.health_fitness.repository.nutrition.MenuRepository;
import com.health_fitness.services.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuPlanService menuPlanService;
    private final UserService userService;
    @PreAuthorize("isAuthenticated()")
    public Menu getMenuToDay(){
        MenuPlan menuPlan = menuPlanService.getMenuPlanToday();
        List<Menu> menu = menuRepository.findByCreatedDate(LocalDate.now()).stream()
                .filter(m ->
                        m.getCreatedBy().equals(userService.getUser())
                        &&m.getMenuPlan().equals(menuPlan))
                .toList();
        return (!menu.isEmpty())? menu.get(0):createMenu(menuPlanService.getMenuPlanToday());
    }

    @PreAuthorize("isAuthenticated()")
    public Menu createMenu(MenuPlan menuPlan) {
        Menu menu = Menu.builder()
                .menuPlan(menuPlan)
                .status(Menu.MenuStatus.IN_PROGRESS)
                .actualTotalCalories(0F)
                .actualTotalCarb(0F)
                .actualTotalProtein(0F)
                .actualTotalFat(0F)
                .notes("")
                .build();
        return menuRepository.save(menu);
    }

    @PreAuthorize("isAuthenticated()")
    public Menu getMenuById(int menuId) {
        return menuRepository.findById(menuId).orElseThrow(() -> new NotFoundException("Menu's not found"));
    }

    @PreAuthorize("isAuthenticated()")
    public Page<Menu> getMenu(Pageable pageable) {
        return menuRepository.findAllByUser(userService.getUser(), pageable);
    }

    public Menu saveMenu(Menu menu) {
        Menu todayMenu = this.getMenuToDay();
        if(todayMenu!=null) menuRepository.delete(todayMenu);
        menu.setId(null);
        menu.setMenuPlan(this.menuPlanService.getMenuPlanToday());
        return menuRepository.save(menu);
    }
}
