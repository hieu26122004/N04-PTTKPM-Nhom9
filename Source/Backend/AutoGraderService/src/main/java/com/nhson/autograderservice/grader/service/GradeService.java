package com.nhson.autograderservice.grader.service;

import com.nhson.autograderservice.exam.entities.Exam;
import com.nhson.autograderservice.exam.services.ExamService;
import com.nhson.autograderservice.exam.services.QuestionService;
import com.nhson.autograderservice.exam.entities.Type;
import com.nhson.autograderservice.exceptions.ExamException;
import com.nhson.autograderservice.grader.model.Answer;
import com.nhson.autograderservice.grader.model.Attempt;
import com.nhson.autograderservice.exam.entities.Question;
import com.nhson.autograderservice.grader.model.Result;
import com.nhson.autograderservice.grader.repository.AttemptRepository;
import com.nhson.autograderservice.user.UserServiceClient;
import com.nhson.autograderservice.user.services.PermissionService;
import org.hibernate.Hibernate;
import org.jsoup.nodes.Document;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.jsoup.Jsoup;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GradeService {
    private final QuestionService questionService;
    private final AttemptRepository attemptRepository;
    private final ExamService examService;
    private final PermissionService permissionService;

    public GradeService(QuestionService questionService, AttemptRepository attemptRepository, ExamService examService, UserServiceClient userServiceClient, PermissionService permissionService) {
        this.questionService = questionService;
        this.attemptRepository = attemptRepository;
        this.examService = examService;
        this.permissionService = permissionService;
    }

    @Transactional
    public Attempt gradeAttempt(JwtAuthenticationToken authenticationToken, Attempt attempt) {
        String examId = attempt.getExamId();
        List<Question> questions = questionService.getQuestionByExamId(examId);
        List<Answer> answers = attempt.getUserAnswer();
        Exam exam = examService.getExamByExamId(examId);
        if (exam.getDue() != null && exam.getDue().isBefore(LocalDateTime.now())) {
            throw new ExamException("This exam is overdue and cannot be graded.", "EXAM_OVERDUE");
        }
        String userId = authenticationToken.getToken().getSubject();

        if (questions.size() != answers.size()) {
            throw new ExamException("The number of answers does not match the number of questions.", "INVALID_ATTEMPT");
        }
        if(Type.PRIVATE.equals(exam.getType())) {
            String clazzId = exam.getClassId();
            if(!permissionService.hasPermissionToSubmit(clazzId,authenticationToken)){
                throw new ExamException("You don't have permission to access this resource.", "PERMISSION_DENIED");
            }
//            if (!permissions.contains(clazzId + ".STUDENT") && !permissions.contains(clazzId + ".TEACHER")) {
//                throw new ExamException("You don't have permission to access this resource.", "PERMISSION_DENIED");
//            }
        }

        for(int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            Answer answer = answers.get(i);
            Hibernate.initialize( question.getOptions());
            String correctAnswer = question.getCorrectOption().getContent();
            String userAnswer = answer.getUserAnswer();
            if(isCorrectAnswer(correctAnswer, userAnswer)) {
                answer.setIsCorrect(true);
            }else{
                answer.setIsCorrect(false);
            }
        }
        attempt.setAttemptId(UUID.randomUUID().toString());
        attempt.setResult(new Result());
        attempt.setQuestions(questions);
        attempt.setUserId(userId);
        attempt.calculateResult();
        attemptRepository.save(attempt);
        return attempt;
    }

    boolean isCorrectAnswer(String correctAnswer, String userAnswer) {
        Document doc1 = Jsoup.parse(correctAnswer);
        Document doc2 = Jsoup.parse(userAnswer);

        String cleanHtml1 = doc1.body().text().replaceAll("\\s+", " ").trim();
        String cleanHtml2 = doc2.body().text().replaceAll("\\s+", " ").trim();

        return cleanHtml1.equals(cleanHtml2);
    }
}
