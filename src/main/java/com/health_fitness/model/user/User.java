package com.health_fitness.model.user;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String email;

    private String username;

    private String password;

    private LocalDate birthDay;

    @Transient
    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDateTime joinDate;

    private LocalDateTime lastLogin;

    @Enumerated(EnumType.ORDINAL)
    private ActivityLevel activityLevel;

    @OneToOne
    private Goal goal;

    @Transient
    private String token;

    public enum Gender { male, female }
    public enum ActivityLevel { beginner, intermediate, advanced }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPlan> userPlans;

    public Integer getAge() {
        return LocalDate.now().getYear() - this.birthDay.getYear();
    }
}
