package com.nhson.examservice.exam.listener;

import com.nhson.examservice.comment.events.CommentCreateEvent;
import com.nhson.examservice.comment.events.CommentDeleteEvent;
import com.nhson.examservice.exam.entities.Exam;
import com.nhson.examservice.exam.repositories.ExamRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ExamCommentListener {
    private final ExamRepository examRepository;
    public ExamCommentListener(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }
    @EventListener
    public void handleExamCommentCreate(CommentCreateEvent commentCreateEvent) {
        Exam exam = examRepository.findById(commentCreateEvent.getComment().getExamId())
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with ID: " + commentCreateEvent.getComment().getExamId()));
        exam.setCommentCount(exam.getCommentCount() + 1);
        examRepository.save(exam);
    }
    @EventListener
    public void handleExamCommentDelete(CommentDeleteEvent commentDeleteEvent) {
        Exam exam = examRepository.findById(commentDeleteEvent.getComment().getExamId())
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with ID: " + commentDeleteEvent.getComment().getExamId()));
        int currentCount = exam.getCommentCount();
        if (currentCount > 0) {
            exam.setCommentCount(currentCount - 1);
        }
        examRepository.save(exam);
    }
}
