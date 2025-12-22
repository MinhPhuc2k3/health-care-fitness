package com.health_fitness.repository.nutrition;

import com.health_fitness.model.user.User;
import com.health_fitness.model.nutrition.ChatHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findTop3ByCreatedByOrderByIdDesc(User user);
    Page<ChatHistory> findByCreatedByOrderByIdDesc(User user, Pageable pageable);
}
