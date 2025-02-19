package com.nhson.examservice.exam.listener;

import com.nhson.examservice.exam.event.ExamActiveEvent;
import com.nhson.examservice.exam.event.ExamDeletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ExamEventListener {

    @EventListener
    public void handleExamDeletedEvent(ExamDeletedEvent event) {
        System.out.println("Bài kiểm tra " + event.getExam() + " đã bị xóa.");
    }

    @EventListener
    public void handleExamActiveEvent(ExamActiveEvent event){
        // TODO
    }
}