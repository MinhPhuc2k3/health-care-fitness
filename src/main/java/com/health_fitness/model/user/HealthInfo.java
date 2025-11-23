package com.health_fitness.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_info")
@Getter
@Setter
@NoArgsConstructor
public class HealthInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Positive
    private Float height;

    @NotNull
    @Positive
    private Float weight;
    private Float bmi;
    private Float bmr;
    private Float fatPercentage;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
