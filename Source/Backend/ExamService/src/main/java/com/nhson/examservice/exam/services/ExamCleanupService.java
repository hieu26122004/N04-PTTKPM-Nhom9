package com.nhson.examservice.exam.services;

import com.nhson.examservice.exam.event.ExamDeletedEvent;
import com.nhson.examservice.exam.entities.Exam;
import com.nhson.examservice.exam.repositories.ExamRepository;
import com.nhson.examservice.question.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExamCleanupService {
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Scheduled(cron = "0 0 1 * * ?")
    public void removeInactiveExams() {
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(30);
        List<Exam> inactiveExams = examRepository.findByCreatedDateBeforeAndIsActiveFalse(thresholdDate);
        if (!inactiveExams.isEmpty()) {
            for (Exam exam : inactiveExams) {
                eventPublisher.publishEvent(new ExamDeletedEvent(this, exam));
            }
        }
    }
}
