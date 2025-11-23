package com.health_fitness.services.workout;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.workout.PlanSession;
import com.health_fitness.repository.workout.PlanSessionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Transactional
public class PlanSessionService {
    private  final PlanSessionRepository planSessionRepository;

    @PreAuthorize("isAuthenticated()")
    public PlanSession getPlanSession(int planSessionId) {
        return planSessionRepository.findById(planSessionId).orElseThrow(()->new NotFoundException("PlanSession's not found"));
    }

}
