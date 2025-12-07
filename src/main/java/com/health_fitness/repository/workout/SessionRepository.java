package com.health_fitness.repository.workout;

import com.health_fitness.model.user.User;
import com.health_fitness.model.workout.PlanSession;
import com.health_fitness.model.workout.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
    @Query("SELECT s FROM Session s WHERE DATE(s.createdDate) = :createdDate AND s.createdBy = :user AND s.planSession =:planSession")
    Session findByCreatedDate(LocalDate createdDate, User user, PlanSession planSession);
}