package com.health_fitness.controllers.nutrition;

import com.health_fitness.model.nutrition.Menu;
import com.health_fitness.services.nutrition.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
class MenuController {
    private final MenuService menuService;

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
}

