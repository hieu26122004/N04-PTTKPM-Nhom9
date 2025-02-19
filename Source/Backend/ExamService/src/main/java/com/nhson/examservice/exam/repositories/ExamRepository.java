package com.nhson.examservice.exam.repositories;

import com.nhson.examservice.exam.entities.Exam;
import com.nhson.examservice.exam.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam,String>, ExamRepositoryCustom {
    List<Exam> findByClassId(String classId);
    void deleteAllByClassId(String classId);
    List<Exam> findByStatus(Status status);
}
