package com.health_fitness.model.nutrition;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class AiConversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer userId;

    @Column(columnDefinition = "TEXT")
    private String userMessage;

    @Column(columnDefinition = "TEXT")
    private String aiResponseJson;

    @OneToOne
    @JoinColumn(name = "menu_id")
    private Menu generatedMenu; // Liên kết trực tiếp với Menu Entity của bạn

    private LocalDateTime timestamp = LocalDateTime.now();
}
