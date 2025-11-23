package com.health_fitness.controllers.quiz;

import com.health_fitness.model.quiz.Quiz;
import com.health_fitness.model.quiz.UserAnswer;
import com.health_fitness.services.quiz.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/v1/quiz")
public class QuizController {

    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public ResponseEntity<Quiz> getQuiz() {
        Quiz quiz = quizService.getQuiz();
        return ResponseEntity.ok(quiz);
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitAnswer(@RequestBody UserAnswer userAnswer) {
        quizService.submitAnswer(userAnswer);
        return ResponseEntity.ok("Quiz answers submitted and processed successfully.");
    }
}