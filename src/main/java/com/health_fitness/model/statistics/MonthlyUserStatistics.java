package com.health_fitness.model.statistics;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_user_statistics")
@Getter
@Immutable
public class MonthlyUserStatistics {

    @Id
    private LocalDateTime month;

    @Column(name = "new_users")
    private Long newUsers;

    @Column(name = "active_users")
    private Long activeUsers;
}
