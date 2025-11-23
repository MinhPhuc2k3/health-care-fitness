package com.health_fitness.model.quiz;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserAnswerQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_answer_id")
    @JsonIgnore
    private UserAnswer userAnswer;

    @Column(name = "question_id")
    private Integer questionId;

    @OneToMany(mappedBy = "userAnswerQuestion", cascade = CascadeType.ALL)
    private List<UserAnswerOption> userAnswerOption;

    private Integer point;
}