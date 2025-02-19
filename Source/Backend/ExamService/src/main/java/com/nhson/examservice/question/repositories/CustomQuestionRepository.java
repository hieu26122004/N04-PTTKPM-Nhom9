package com.nhson.examservice.question.repositories;

import com.nhson.examservice.question.entities.Question;

import java.util.List;

public interface CustomQuestionRepository {
    void saveAllQuestions(List<Question> questions);
    void saveQuestion(Question question);
}
