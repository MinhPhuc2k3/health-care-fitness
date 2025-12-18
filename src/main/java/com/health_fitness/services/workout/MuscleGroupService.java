package com.health_fitness.services.workout;

import com.health_fitness.model.workout.MuscleGroup;
import com.health_fitness.repository.workout.MuscleGroupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MuscleGroupService {
    private final MuscleGroupRepository muscleGroupRepository;

    public List<MuscleGroup> getMuscleGroupByIds(List<Integer> muscleGroupIds){
        return muscleGroupRepository.findAllById(muscleGroupIds);
    }

    public List<MuscleGroup> getMuscleGroup(){
        return muscleGroupRepository.findAll();
    }
}
