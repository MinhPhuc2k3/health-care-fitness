package com.health_fitness.controllers.workout;

import com.health_fitness.model.workout.MuscleGroup;
import com.health_fitness.repository.workout.MuscleGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/muscle_group")
@RequiredArgsConstructor
public class MuscleGroupController {
    private final MuscleGroupRepository muscleGroupRepository;

    @GetMapping
    public List<MuscleGroup> getMuscleGroup(){
        return muscleGroupRepository.findAll();
    }
}
