package com.health_fitness.services.workout;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.workout.Session;
import com.health_fitness.model.workout.SessionExercise;
import com.health_fitness.repository.workout.SessionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
@Transactional
public class SessionService {
    private final SessionRepository sessionRepository;
    private final PlanSessionService planSessionService;

    @PreAuthorize("isAuthenticated()")
    public Session getSession(int sessionId) {
        return sessionRepository.findById(sessionId).orElseThrow(() -> new NotFoundException("Session not found"));
    }
    @PreAuthorize("isAuthenticated()")
    public Session createSession(Session session) {
        Session sessionToSave = Session.builder()
                .planSession(planSessionService.getPlanSession(session.getPlanSession().getId()))
                .status(Session.SessionStatus.IN_PROGRESS)
                .build();
        return sessionRepository.save(sessionToSave);
    }
    @PreAuthorize("isAuthenticated()")
    public List<SessionExercise> getSessionExercises(int sessionId) {
        return getSession(sessionId).getSessionExercises();
    }

}
