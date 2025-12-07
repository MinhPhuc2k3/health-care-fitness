package com.health_fitness.services.workout;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.workout.PlanSession;
import com.health_fitness.repository.workout.PlanSessionRepository;
import com.health_fitness.services.user.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;

@AllArgsConstructor
@Service
@Transactional
public class PlanSessionService {
    private  final PlanSessionRepository planSessionRepository;
    private final UserPlanService userPlanService;

    @PreAuthorize("isAuthenticated()")
    public PlanSession getPlanSession(int planSessionId) {
        return planSessionRepository.findById(planSessionId).orElseThrow(()->new NotFoundException("PlanSession's not found"));
    }

    public PlanSession getPlanSessionByDay(DayOfWeek dayOfWeek){
        List<PlanSession> planSessionList = userPlanService.getActiveUserPlan().getPlan().getPlanSessions();
        for(PlanSession planSession:planSessionList){
            if(planSession.getSessionDayOfWeek() == dayOfWeek){
                return planSession;
            }
        }
        return null;
    }
}
