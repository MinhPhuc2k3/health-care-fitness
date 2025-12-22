package com.health_fitness.model.nutrition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.health_fitness.model.user.Auditable;
import com.health_fitness.model.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatHistory extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(columnDefinition = "TEXT")
    private String userPrompt;

    @Column(columnDefinition = "TEXT")
    private String systemPrompt;

    @Column(columnDefinition = "TEXT")
    private String aiResponse;

    @Enumerated(EnumType.STRING)
    private ChatCategory category;

    @Column(name = "menu_id")
    private Integer relatedMenuId;

    public enum ChatCategory {
        MENU_GENERATION, NUTRITION_ADVICE, GENERAL
    }
}
