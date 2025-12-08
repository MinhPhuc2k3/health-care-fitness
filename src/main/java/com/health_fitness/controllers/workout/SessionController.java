package com.health_fitness.controllers.workout;

import com.health_fitness.model.workout.Session;
import com.health_fitness.model.workout.SessionExercise;
import com.health_fitness.services.workout.SessionService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("/today")
    public Session getSessionToday() {
        return sessionService.getSessionToday();
    }

    @GetMapping("/{sessionId}")
    public Session getSession(@PathVariable int sessionId) {
        return sessionService.getSession(sessionId);
    }

    @PostMapping
    public Session createSession(@Valid @RequestBody Session session) {
        return sessionService.createSession(session);
    }

    @GetMapping("/{sessionId}/exercises")
    public List<SessionExercise> getSessionExercises(@PathVariable int sessionId) {
        return sessionService.getSessionExercises(sessionId);
    }

    @PutMapping("/{sessionId}")
    public Session updateSession(@PathVariable int sessionId, @RequestBody Session session){
        return sessionService.updateSession(sessionId,session);
    }
}