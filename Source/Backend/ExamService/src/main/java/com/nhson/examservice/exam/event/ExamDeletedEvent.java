package com.nhson.examservice.exam.event;

import com.nhson.examservice.exam.entities.Exam;
import org.springframework.context.ApplicationEvent;

public class ExamDeletedEvent extends ApplicationEvent {
    private final Exam exam;

    public ExamDeletedEvent(Object source, Exam exam) {
        super(source);
        this.exam = exam;
    }

    public Exam getExam() {
        return exam;
    }
}
