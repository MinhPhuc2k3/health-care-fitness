package com.health_fitness.services.workout;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.nutrition.MenuPlan;
import com.health_fitness.model.user.HealthInfo;
import com.health_fitness.model.user.UserPlan;
import com.health_fitness.model.workout.Plan;
import com.health_fitness.model.workout.PlanSession;
import com.health_fitness.repository.workout.UserPlanRepository;
import com.health_fitness.services.user.HealthInfoService;
import com.health_fitness.services.user.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional
public class UserPlanService {
    private final UserPlanRepository userPlanRepository;
    private final PlanService planService;
    private final UserService  userService;
    private final HealthInfoService healthInfoService;

    @PreAuthorize("isAuthenticated()")
    public UserPlan addUserPlan(Plan planAdd){
        Plan plan = planService.getPlan(planAdd.getId());
        UserPlan userPlanToSave = UserPlan.builder()
                .plan(plan)
                .status(UserPlan.PlanStatus.ACTIVE)
                .startDate(LocalDate.now())
                .build();
        userService.getUser().getUserPlans().forEach(userPlan -> {
            userPlan.setEndDate(LocalDate.now());
            userPlan.setStatus(UserPlan.PlanStatus.COMPLETED);
        });
        this.generateMenuPlans(plan, userPlanToSave);
        return userPlanRepository.save(userPlanToSave);
    }

    @PreAuthorize("isAuthenticated()")
    public UserPlan getActiveUserPlan(){
        List<UserPlan> userPlans = userService.getUser().getUserPlans().stream().filter(userPlan -> userPlan.getStatus()
                .equals(UserPlan.PlanStatus.ACTIVE))
                .sorted((o1, o2) -> o1.getStartDate().isAfter(o2.getStartDate())? 1:-1)
                .toList();
        if(userPlans.isEmpty()) throw new NotFoundException("User plan not found");
        return userPlans.get(0);
    }


    private void generateMenuPlans(Plan plan, UserPlan userPlan){
        HealthInfo healthInfo = healthInfoService.findLastestHealthInfo();
        float bmr = healthInfo.getBmr();
        Map<DayOfWeek, PlanSession> planSessionMap = plan.getPlanSessions().stream().collect(Collectors.toMap(PlanSession::getSessionDayOfWeek, o->o));
        for(int i=1; i<=7; i++){
            DayOfWeek currentDay = DayOfWeek.of(i);
            MenuPlan menuPlan = null;
            PlanSession planSession = planSessionMap.get(currentDay);
            float tdee = bmr + ((planSession != null)? planSession.getTargetCalories():0F);
            float proteinGrams = healthInfo.getWeight() * 2.2F;
            float proteinCalories = proteinGrams * 4;
            switch (plan.getGoal().getName()) {
                case BUILD_MUSCLE -> {
                    float targetEatCalories = tdee * 1.15F;
                    if (planSession!=null && planSession.getTargetCalories() != 0) {
                        // Training day: High carb, low fat
                        float fatPercentage = 0.2F;
                        float fatCalories = targetEatCalories * fatPercentage;
                        float fatGrams = fatCalories / 9;

                        float carbCalories = targetEatCalories - proteinCalories - fatCalories;
                        float carbGrams = carbCalories / 4;

                        menuPlan = MenuPlan.builder()
                                .dayOfWeek(planSession.getSessionDayOfWeek())
                                .targetCalories(targetEatCalories)
                                .targetProtein(proteinGrams)
                                .targetFat(fatGrams)
                                .targetCarb(carbGrams)
                                .userPlan(userPlan)
                                .build();
                    } else {
                        // Rest day: Lower calories, higher fat, lower carbs
                        float restDayCalories = targetEatCalories * 0.8F;
                        float fatPercentage = 0.35F;
                        float fatCalories = restDayCalories * fatPercentage;
                        float fatGrams = fatCalories / 9;
                        float carbCalories = restDayCalories - proteinCalories - fatCalories;
                        float carbGrams = carbCalories / 4;

                        menuPlan = MenuPlan.builder()
                                .dayOfWeek(currentDay)
                                .targetCalories(restDayCalories)
                                .targetProtein(proteinGrams)
                                .targetFat(fatGrams)
                                .targetCarb(carbGrams)
                                .userPlan(userPlan)
                                .build();
                    }
                }
                case LOSS_WEIGHT -> {
                    float targetCalories = tdee * 0.75F;
                    if (planSession!=null && planSession.getTargetCalories() != 0) {
                        float fatPercentage = 0.20F;
                        float fatCalories = targetCalories * fatPercentage;
                        float fatGrams = fatCalories / 9;

                        float carbCalories = targetCalories - proteinCalories - fatCalories;
                        float carbGrams = carbCalories / 4;

                        menuPlan = MenuPlan.builder()
                                .dayOfWeek(planSession.getSessionDayOfWeek())
                                .targetCalories(targetCalories)
                                .targetProtein(proteinGrams)
                                .targetFat(fatGrams)
                                .targetCarb(carbGrams)
                                .userPlan(userPlan)
                                .build();
                    } else {
                        // Rest day: Lower calories, higher fat
                        float restDayCalories = targetCalories * 0.8F;

                        float fatPercentage = 0.35F; // 35% fat on rest days
                        float fatCalories = restDayCalories * fatPercentage;
                        float fatGrams = fatCalories / 9;

                        float carbCalories = restDayCalories - proteinCalories - fatCalories;
                        float carbGrams = carbCalories / 4;

                        menuPlan = MenuPlan.builder()
                                .dayOfWeek(currentDay)
                                .targetCalories(restDayCalories)
                                .targetProtein(proteinGrams)
                                .targetFat(fatGrams)
                                .targetCarb(carbGrams)
                                .userPlan(userPlan)
                                .build();
                    }
                }
                case MAINTAIN_WEIGHT -> {
                    // Maintenance: Calories = TDEE
                    float targetCalories = tdee;
                    // Training day
                    if (planSession!=null && planSession.getTargetCalories() != 0) {
                        float fatPercentage = 0.25F;
                        float fatCalories = targetCalories * fatPercentage;
                        float fatGrams = fatCalories / 9;

                        float carbCalories = targetCalories - proteinCalories - fatCalories;
                        float carbGrams = carbCalories / 4;

                        menuPlan = MenuPlan.builder()
                                .dayOfWeek(planSession.getSessionDayOfWeek())
                                .targetCalories(targetCalories)
                                .targetProtein(proteinGrams)
                                .targetFat(fatGrams)
                                .targetCarb(carbGrams)
                                .userPlan(userPlan)
                                .build();
                    } else {
                        // Rest day
                        float fatPercentage = 0.30F; // 30% fat
                        float fatCalories = targetCalories * fatPercentage;
                        float fatGrams = fatCalories / 9;

                        float carbCalories = targetCalories - proteinCalories - fatCalories;
                        float carbGrams = carbCalories / 4;

                        menuPlan = MenuPlan.builder()
                                .dayOfWeek(currentDay)
                                .targetCalories(targetCalories)
                                .targetProtein(proteinGrams)
                                .targetFat(fatGrams)
                                .targetCarb(carbGrams)
                                .userPlan(userPlan)
                                .build();
                    }
                }
            }
            if(menuPlan!=null) userPlan.getMenuPlans().add(menuPlan);
        }
        plan.getPlanSessions().forEach(
                planSession -> {





                }
        );
    }
}
