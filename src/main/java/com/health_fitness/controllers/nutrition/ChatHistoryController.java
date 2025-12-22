package com.health_fitness.controllers.nutrition;

import com.health_fitness.model.nutrition.ChatHistory;
import com.health_fitness.model.nutrition.Menu;
import com.health_fitness.services.nutrition.ChatHistoryService;
import com.health_fitness.services.nutrition.GeminiMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-history")
public class ChatHistoryController {
    private final GeminiMenuService geminiMenuService;
    private final ChatHistoryService chatHistoryService;

    @PostMapping
    public Menu saveMenuFromChat(@RequestBody Map<String, Long> chatId){
        return geminiMenuService.saveMenuFromChat(chatId.get("chatId"));
    }

    @GetMapping
    public Page<ChatHistory> chatHistories(Pageable pageable){
        return chatHistoryService.findALl(pageable);
    }


    @GetMapping("{id}/menu")
    public Menu getMenuFromChat(@PathVariable Long id){
        return geminiMenuService.getMenuFromChat(id);
    }
}
