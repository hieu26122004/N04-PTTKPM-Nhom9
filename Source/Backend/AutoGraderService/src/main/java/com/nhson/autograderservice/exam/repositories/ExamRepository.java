package com.nhson.autograderservice.exam.repositories;

import com.nhson.autograderservice.exam.entities.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam,String> {
    public Optional<Exam> findByExamId(String name);
}
