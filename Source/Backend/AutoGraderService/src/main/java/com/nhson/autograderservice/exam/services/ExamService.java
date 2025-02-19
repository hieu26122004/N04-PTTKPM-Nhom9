package com.nhson.autograderservice.exam.services;

import com.nhson.autograderservice.exam.entities.Exam;
import com.nhson.autograderservice.exam.repositories.ExamRepository;
import org.springframework.stereotype.Service;

@Service
public class ExamService {

    private final ExamRepository examRepository;

    public ExamService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    public Exam getExamByExamId(String examId) {
        return examRepository.findByExamId(examId).orElseThrow(() -> new IllegalArgumentException("Cannot find exam with id: " + examId));
    }
}
