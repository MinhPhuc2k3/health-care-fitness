package com.health_fitness.model.quiz;

import com.health_fitness.model.user.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserAnswer extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer quizId;

    @OneToMany(mappedBy = "userAnswer", cascade = CascadeType.ALL)
    private List<UserAnswerQuestion> userAnswerQuestions;

    private Integer totalScore;

}
