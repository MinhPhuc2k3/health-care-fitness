package com.health_fitness.repository.user;

import com.health_fitness.model.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    @Query("Select u From User u LEFT JOIN u.roles r LEFT JOIN r.permissions WHERE u.username = :username OR u.email = :email")
    Optional<User> findByUsernameOrEmail(String username, String email);
}
