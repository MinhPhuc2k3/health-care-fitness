package com.health_fitness.controllers.workout;
import com.health_fitness.model.workout.RunSession;
import com.health_fitness.services.workout.RunSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/run-sessions")
@RequiredArgsConstructor
public class RunSessionController {

    private final RunSessionService service;

    @PostMapping
    public RunSession create(@RequestBody RunSession runSession) {
        return service.create(runSession);
    }

    @GetMapping
    public List<RunSession> getMyRunSessions() {
        return service.getMyRunSessions();
    }
}
