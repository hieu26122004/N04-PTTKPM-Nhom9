package com.nhson.autograderservice.exam.services;

import com.nhson.autograderservice.exam.repositories.QuestionRepository;
import com.nhson.autograderservice.exam.entities.Question;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<Question> getQuestionByExamId(String examId) {
        List<Question> questions = questionRepository.findAllByExamId(examId);
        return questions;
    }

}
