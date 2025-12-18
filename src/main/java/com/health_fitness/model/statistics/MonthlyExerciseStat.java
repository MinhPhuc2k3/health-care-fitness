package com.health_fitness.model.statistics;
import com.health_fitness.model.statistics.classid.MonthlyExerciseStatId;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_exercise_stat")
@Getter
@Immutable
@IdClass(MonthlyExerciseStatId.class)
public class MonthlyExerciseStat {

    @Id
    private LocalDateTime month;

    @Id
    @Column(name = "exercise_id")
    private Integer exerciseId;

    private String name;

    private String image_url;

    @Column(name = "total_sessions")
    private Long totalSessions;

    private Integer rank;
}
