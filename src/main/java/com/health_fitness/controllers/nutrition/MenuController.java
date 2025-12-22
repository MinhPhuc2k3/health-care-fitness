package com.health_fitness.controllers.nutrition;

import com.health_fitness.model.nutrition.ChatHistory;
import com.health_fitness.model.nutrition.Meal;
import com.health_fitness.model.nutrition.Menu;
import com.health_fitness.model.user.User;
import com.health_fitness.services.nutrition.GeminiMenuService;
import com.health_fitness.services.nutrition.MenuAutoFillService;
import com.health_fitness.services.nutrition.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
class MenuController {
    private final MenuService menuService;
    private final MenuAutoFillService  menuAutoFillService;

    @GetMapping("/today")
    public ResponseEntity<Menu> getMenuToday() {
        Menu menu = menuService.getMenuToDay();
        return ResponseEntity.status(HttpStatus.OK).body(menu);
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<Menu> getMenu(@PathVariable int menuId) {
        Menu menu = menuService.getMenuById(menuId);
        return ResponseEntity.ok(menu);
    }

    public ResponseEntity<Page<Menu>> getAllMenu(Pageable pageable){
        return ResponseEntity.ok(menuService.getMenu(pageable));
    }

    @PostMapping("/{menuId}/auto-fill")
    public ResponseEntity<Menu> autoFillMenu(@PathVariable int menuId) {
        Menu filledMenu = menuAutoFillService.autoFillMenu(menuId);
        return ResponseEntity.ok(filledMenu);
    }

    private final GeminiMenuService geminiMenuService;

    @PostMapping("/recommend-chat")
    public ResponseEntity<ChatHistory> getDailyRecommendation(@RequestBody  String userPrompt) {
        ChatHistory chatHistory = geminiMenuService.generateDailyMenuSuggestion(userPrompt);
        return ResponseEntity.ok(chatHistory);
    }

}

