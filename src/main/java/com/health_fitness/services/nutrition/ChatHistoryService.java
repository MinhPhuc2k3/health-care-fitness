package com.health_fitness.services.nutrition;

import com.health_fitness.model.nutrition.ChatHistory;
import com.health_fitness.repository.nutrition.ChatHistoryRepository;
import com.health_fitness.services.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Transactional
@RequiredArgsConstructor
public class ChatHistoryService {
    private final ChatHistoryRepository chatHistoryRepository;
    private final UserService userService;

    public Page<ChatHistory> findALl(Pageable pageable){
        return this.chatHistoryRepository.findByCreatedByOrderByIdDesc(userService.getUser(), pageable);
    }
}
