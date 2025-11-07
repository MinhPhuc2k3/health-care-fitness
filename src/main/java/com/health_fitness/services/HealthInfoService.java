package com.health_fitness.services;

import com.health_fitness.config.security.CustomUserDetails;
import com.health_fitness.exception.BadRequestException;
import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.user.HealthInfo;
import com.health_fitness.model.user.User;
import com.health_fitness.repository.HealthInfoRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@Transactional
public class HealthInfoService {

    private final HealthInfoRepository healthInfoRepository;

    public HealthInfoService(HealthInfoRepository healthInfoRepository) {
        this.healthInfoRepository = healthInfoRepository;
    }

    @PreAuthorize("isAuthenticated()")
    public HealthInfo saveHealthInfo(HealthInfo healthInfo) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        validateHealthInfo(healthInfo);
        float height = healthInfo.getHeight();
        float weight = healthInfo.getWeight();
        float bmi = weight / (float) Math.pow(height / 100, 2);
        float bmr;
        if (user.getGender() == User.Gender.male)
            bmr = (float) (10 * weight + 6.25 * height - 5 * user.getAge() + 5);
        else
            bmr = (float) (10 * weight + 6.25 * height - 5 * user.getAge() - 161);

        double activityFactor = switch (user.getActivityLevel()) {
            case sedentary -> 1.2;
            case light -> 1.375;
            case moderate -> 1.55;
            case active -> 1.725;
            case very_active -> 1.9;
        };

        float tdee = (float) (bmr * activityFactor);
        healthInfo.setUser(user);
        healthInfo.setBmi(bmi);
        healthInfo.setTdee(tdee);
        healthInfo.setCreatedDate(LocalDateTime.now());
        healthInfo.setUpdatedDate(LocalDateTime.now());

        return healthInfoRepository.save(healthInfo);
    }

    private void validateHealthInfo(HealthInfo healthInfo) {
        if (healthInfo == null) {
            throw new BadRequestException("Health information cannot be null.");
        }
        if (healthInfo.getHeight() == null || healthInfo.getHeight() <= 0) {
            throw new BadRequestException("Height must be greater than 0.");
        }
        if (healthInfo.getWeight() == null || healthInfo.getWeight() <= 0) {
            throw new BadRequestException("Weight must be greater than 0.");
        }
        if (healthInfo.getHeight() < 100 || healthInfo.getHeight() > 250) {
            throw new BadRequestException("Height value seems unrealistic (should be between 100 and 250 cm).");
        }
        if (healthInfo.getWeight() < 30 || healthInfo.getWeight() > 250) {
            throw new BadRequestException("Weight value seems unrealistic (should be between 30 and 250 kg).");
        }
    }

    @PreAuthorize("isAuthenticated()")
    public HealthInfo findLastestHealthInfo() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        return getAllHealthInfo(0,1).getContent().get(0);
    }

    @PreAuthorize("isAuthenticated()")
    public Page<HealthInfo> getAllHealthInfo(int page, int size) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        Page<HealthInfo> healthInfos = healthInfoRepository.getHealthInfoByUserId(PageRequest.of(page, size, Sort.by("createdDate")), user.getId());
        if(healthInfos.isEmpty()) throw new NotFoundException("Could not find any health info");
        return healthInfos;
    }
}
