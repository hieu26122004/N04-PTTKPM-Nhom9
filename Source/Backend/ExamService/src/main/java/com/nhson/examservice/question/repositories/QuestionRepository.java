package com.nhson.examservice.question.repositories;

import com.nhson.examservice.question.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface QuestionRepository extends JpaRepository<Question,String > , CustomQuestionRepository {
    public List<Question> findAllByExamId(String examId);

    public List<Question> findByExamIdOrderByQuestionOrderAsc(String examId);
}
