package com.nhson.examservice.exam.event;

import com.nhson.examservice.exam.entities.Exam;
import org.springframework.context.ApplicationEvent;

public class ExamActiveEvent extends ApplicationEvent {
    private final Exam exam;

    public ExamActiveEvent(Object source,Exam exam) {
        super(source);
        this.exam = exam;
    }


    public Exam getExam() {
        return exam;
    }
}