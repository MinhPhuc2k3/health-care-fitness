package com.health_fitness.repository.nutrition;

import com.health_fitness.model.meal.Menu;
import com.health_fitness.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    @Query("SELECT m FROM Menu m WHERE DATE(m.createdDate) =:createdDate")
    List<Menu> findByCreatedDate(LocalDate createdDate);

    @Query("SELECT m FROM Menu m WHERE DATE(m.createdBy) =:user")
    Page<Menu> findAllByUser(User user, Pageable pageable);
}