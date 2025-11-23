package com.health_fitness.services.quiz;

import com.health_fitness.exception.BadRequestException;
import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.quiz.*;
import com.health_fitness.model.user.User;
import com.health_fitness.repository.quiz.QuestionRepository;
import com.health_fitness.repository.quiz.QuizRepository;
import com.health_fitness.repository.quiz.UserAnswerRepository;
import com.health_fitness.repository.user.UserRepository;
import com.health_fitness.services.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepo;
    private final QuestionRepository questionRepo;
    private final UserAnswerRepository userAnswerRepo;
    private final UserService userService;
    private final UserRepository userRepo;

    @Autowired
    public QuizService(QuizRepository quizRepo, QuestionRepository questionRepo, UserAnswerRepository userAnswerRepo, UserService userService, UserRepository userRepo) {
        this.quizRepo = quizRepo;
        this.questionRepo = questionRepo;
        this.userAnswerRepo = userAnswerRepo;
        this.userService = userService;
        this.userRepo = userRepo;
    }

    public Quiz getQuiz() {
        List<Quiz> quizList = quizRepo.findAll();
        if (quizList.isEmpty()) throw new NotFoundException("quiz not found!");
        return quizList.get(0);
    }

    @Transactional
    public void submitAnswer(UserAnswer userAnswer) {
        Quiz quiz = quizRepo.findById(userAnswer.getQuizId()).orElseThrow(() -> new NotFoundException("quiz not found!"));
        Map<Integer, UserAnswerQuestion> questionMap = userAnswer.getUserAnswerQuestions().stream()
                .collect(Collectors.toMap(
                        UserAnswerQuestion::getQuestionId,
                        userAnswerQuestion -> userAnswerQuestion
                ));
        List<Question> questions = questionRepo.findAllById(questionMap.keySet());
        questions.forEach(
                (question) -> {
                    UserAnswerQuestion userAQ = questionMap.get(question.getId());
                    Map<Integer, UserAnswerOption> userAOMap = userAQ.getUserAnswerOption().stream()
                            .collect(Collectors.toMap(
                                    UserAnswerOption::getOptionId,
                                    userAO -> userAO
                            ));
                    question.getOptions().forEach(
                            op -> {
                                if (userAOMap.containsKey(op.getId())) {
                                    userAQ.setPoint((userAQ.getPoint() == null) ? 0 : userAQ.getPoint() + op.getPoint());
                                    userAnswer.setTotalScore(userAnswer.getTotalScore()==null? 0: userAnswer.getTotalScore() + op.getPoint());
                                }
                            }
                    );
                }
        );
        if(userAnswer.getTotalScore()<0) throw new BadRequestException("total score is negative number");
        if(userAnswer.getTotalScore()<=8){
            userService.getUser().setActivityLevel(User.ActivityLevel.beginner);
        }else if(userAnswer.getTotalScore()<=16){
            userService.getUser().setActivityLevel(User.ActivityLevel.intermediate);
        }else{
            userService.getUser().setActivityLevel(User.ActivityLevel.advanced);
        }
        userAnswer.getUserAnswerQuestions().forEach(userAnswerQuestion -> {
            userAnswerQuestion.getUserAnswerOption().forEach(
                    u -> u.setId(null)
            );
            userAnswerQuestion.setId(null);
        });
        userRepo.save(userService.getUser());
        userAnswerRepo.save(userAnswer);
    }
}
