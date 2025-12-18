package com.health_fitness.model.workout;

import com.health_fitness.model.user.Auditable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "run_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunSession extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer averageHeartRate;

    private Integer totalDistanceMeters;

    private Integer totalCalories;

    private Long durationMillis;

    /**
     * Thời điểm chạy (timestamp từ mobile gửi lên)
     */
    private Long timestampMillis;
}
