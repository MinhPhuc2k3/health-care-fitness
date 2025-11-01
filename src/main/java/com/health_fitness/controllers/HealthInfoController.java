package com.health_fitness.controllers;

import com.health_fitness.model.user.HealthInfo;
import com.health_fitness.services.HealthInfoService;
import jakarta.websocket.server.PathParam;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/v1/health-info")
public class HealthInfoController {
    private final HealthInfoService healthInfoService;

    public HealthInfoController(HealthInfoService healthInfoService) {
        this.healthInfoService = healthInfoService;
    }

    @PostMapping
    public HealthInfo createHealthIfo(@RequestBody  HealthInfo healthInfo){
        return healthInfoService.saveHealthInfo(healthInfo);
    }

    @GetMapping()
    public HealthInfo getLastestHealthInfo(){
        return healthInfoService.findLastestHealthInfo();
    }

    @GetMapping("/all")
    public Page<HealthInfo> getAllHealthInfo(@PathParam("0") int page, @PathParam("10") int size){
        return healthInfoService.getAllHealthInfo(page, size);
    }
}
