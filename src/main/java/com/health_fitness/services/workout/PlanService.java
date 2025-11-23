package com.health_fitness.services.workout;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.meal.MenuPlan;
import com.health_fitness.model.user.HealthInfo;
import com.health_fitness.model.workout.Plan;
import com.health_fitness.model.workout.PlanSession;
import com.health_fitness.repository.workout.PlanRepository;
import com.health_fitness.services.user.HealthInfoService;
import com.health_fitness.services.user.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
@Transactional
public class PlanService {
    private final PlanRepository planRepository;
    private final HealthInfoService healthInfoService;

    public List<PlanSession> getPlanSession(int planId) {
        return getPlan(planId).getPlanSessions();
    }

    @PreAuthorize("isAuthenticated()")
    public Plan getPlan(int planId) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new NotFoundException("Plan not found"));
        if (plan.getMenuPlans().isEmpty()) {
            plan = generateMenuPlans(plan);
        }
        return plan;
    }

    private Plan generateMenuPlans(Plan plan){
        HealthInfo healthInfo = healthInfoService.findLastestHealthInfo();
        float bmr = healthInfo.getBmr();
        plan.getPlanSessions().forEach(
                planSession -> {
                    MenuPlan menuPlan = null;
                    float tdee = bmr + planSession.getTargetCalories();
                    float proteinGrams = healthInfo.getWeight() * 2.2F;
                    float proteinCalories = proteinGrams * 4;

                    switch (plan.getGoal().getName()) {
                        case BUILD_MUSCLE -> {
                            float targetEatCalories = tdee * 1.15F;
                            if (planSession.getTargetCalories() != 0) {
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
                                        .plan(plan)
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
                                        .dayOfWeek(planSession.getSessionDayOfWeek())
                                        .targetCalories(restDayCalories)
                                        .targetProtein(proteinGrams)
                                        .targetFat(fatGrams)
                                        .targetCarb(carbGrams)
                                        .plan(plan)
                                        .build();
                            }
                        }
                        case LOSS_WEIGHT -> {
                            float targetCalories = tdee * 0.75F;
                            if (planSession.getTargetCalories() != 0) {
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
                                        .plan(plan)
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
                                        .dayOfWeek(planSession.getSessionDayOfWeek())
                                        .targetCalories(restDayCalories)
                                        .targetProtein(proteinGrams)
                                        .targetFat(fatGrams)
                                        .targetCarb(carbGrams)
                                        .plan(plan)
                                        .build();
                            }
                        }
                        case MAINTAIN_WEIGHT -> {
                            // Maintenance: Calories = TDEE
                            float targetCalories = tdee;
                            // Training day
                            if (planSession.getTargetCalories() != 0) {
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
                                        .plan(plan)
                                        .build();
                            } else {
                                // Rest day
                                float fatPercentage = 0.30F; // 30% fat
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
                                        .plan(plan)
                                        .build();
                            }
                        }
                    }
                    if(menuPlan!=null) plan.getMenuPlans().add(menuPlan);
                }
        );
        return planRepository.save(plan);
    }
}