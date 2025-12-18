package com.health_fitness.repository.workout.specification;

import com.health_fitness.model.user.User;
import com.health_fitness.model.workout.Exercise;
import com.health_fitness.model.workout.MuscleGroup;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ExerciseSpecification {
    public static Specification<Exercise> findByCategoryMuscleGroupsActivityLevel( final String exerciseName,
            final Exercise.ExerciseCategory category, final List<MuscleGroup> muscleGroups, final User.ActivityLevel activityLevel
    ) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(category != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }
            if(muscleGroups!=null && !muscleGroups.isEmpty()) {
                Join<Exercise, MuscleGroup> mgJoin = root.join("muscleGroups", JoinType.LEFT);
                predicates.add(mgJoin.in(muscleGroups));
            }
            if(activityLevel != null) {
                predicates.add(criteriaBuilder.equal(root.get("difficulty"), activityLevel));
            }
            if(exerciseName != null) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%"+exerciseName.toLowerCase()+"%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%"+exerciseName.toLowerCase()+"%")
                ));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
