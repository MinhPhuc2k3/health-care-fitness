package com.health_fitness.services.workout;


import com.health_fitness.model.workout.RunSession;
import com.health_fitness.repository.workout.RunSessionRepository;
import com.health_fitness.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RunSessionService {

    private final RunSessionRepository repository;
    private final UserService userService;

    public RunSession create(RunSession runSession) {
        return repository.save(runSession);
    }

    public List<RunSession> getMyRunSessions() {
        return repository.findByCreatedBy_Id(userService.getUser().getId());
    }
}

