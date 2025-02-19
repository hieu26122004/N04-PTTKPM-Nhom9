package com.nhson.autograderservice.exam.repositories;

import com.nhson.autograderservice.exam.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {
    public List<Question> findAllByExamId(String examId);

}
