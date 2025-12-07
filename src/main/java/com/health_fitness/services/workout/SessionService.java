package com.health_fitness.services.workout;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.workout.PlanSession;
import com.health_fitness.model.workout.Session;
import com.health_fitness.model.workout.SessionExercise;
import com.health_fitness.repository.workout.SessionRepository;
import com.health_fitness.services.user.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Service
@Transactional
public class SessionService {
    private final SessionRepository sessionRepository;
    private final PlanSessionService planSessionService;
    private final UserService userService;

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

    public Session getSessionToday() {
        PlanSession planSession = planSessionService.getPlanSessionByDay(LocalDate.now().getDayOfWeek());
        Session session= sessionRepository.findByCreatedDate(LocalDate.now(), userService.getUser(), planSession);
        if(session==null){
            session = new Session();
            session.setPlanSession(planSession);
            session.setStatus(Session.SessionStatus.IN_PROGRESS);
            session = sessionRepository.save(session);
        }
        return session;
    }

    @PreAuthorize("isAuthenticated()")
    public Session updateSession(int sessionId, Session session) {
        Session sessionToSave = getSession(sessionId);
        BeanUtils.copyProperties(session, sessionToSave, "id", "planSession", "sessionExercises");
        return sessionRepository.save(sessionToSave);
    }
}
