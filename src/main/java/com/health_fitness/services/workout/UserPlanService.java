package com.health_fitness.services.workout;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.user.UserPlan;
import com.health_fitness.model.workout.Plan;
import com.health_fitness.repository.workout.UserPlanRepository;
import com.health_fitness.services.user.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Service
@Transactional
public class UserPlanService {
    private final UserPlanRepository userPlanRepository;
    private final PlanService planService;
    private final UserService  userService;

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
}
