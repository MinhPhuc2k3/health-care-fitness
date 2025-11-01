package com.health_fitness.repository;

import com.health_fitness.model.user.HealthInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HealthInfoRepository extends JpaRepository<HealthInfo, Long> {
    @Query("SELECT h FROM HealthInfo h WHERE h.user.id= :userId ORDER BY h.createdDate DESC")
    Page<HealthInfo> getHealthInfoByUserId(Pageable pageable, Long userId);
}
