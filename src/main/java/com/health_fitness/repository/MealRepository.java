package com.health_fitness.repository;

import com.health_fitness.model.recipe.meal.Meal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MealRepository extends CrudRepository<Meal, Long> {

    @Query("SELECT m FROM Meal m JOIN m.createdBy u WHERE u.id=:userId ORDER BY m.mealDate")
    Page<Meal> findAll(Pageable pageable, @Param("userId") Long userId);
}
