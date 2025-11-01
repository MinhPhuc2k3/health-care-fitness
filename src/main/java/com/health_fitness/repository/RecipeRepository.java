package com.health_fitness.repository;

import com.health_fitness.model.recipe.meal.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RecipeRepository extends CrudRepository<Recipe, Long> {
    @Query("""
    SELECT DISTINCT r
    FROM Recipe r
    JOIN r.createdBy u
    JOIN u.roles role
    WHERE u.id = :userId OR role.role = 'ADMIN'
""")
    Page<Recipe> findAllByUserOrAdmin(@Param("userId") Long userId, Pageable pageable);
}
