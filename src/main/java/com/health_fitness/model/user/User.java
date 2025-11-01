package com.health_fitness.model.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String username;

    private String password;

    private LocalDate birthDay;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDateTime joinDate;

    private LocalDateTime lastLogin;

    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    private GoalType goal;

    private boolean isPremium;

    @Transient
    private String token;

    public enum Gender { male, female }
    public enum ActivityLevel { sedentary, light, moderate, active, very_active }
    public enum GoalType { fat_loss, muscle_gain, maintain, endurance }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
