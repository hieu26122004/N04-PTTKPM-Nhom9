package com.nhson.autograderservice.grader.model;

import jakarta.persistence.*;
import lombok.Data;

@Embeddable
@Data
public class Answer {

    @Column(name = "exam_id", nullable = false)
    private String examId;

    @Column(name = "question_id", nullable = false)
    private String questionId;

    @Column(name = "user_answer")
    private String userAnswer;

    @Column(name = "is_correct")
    private Boolean isCorrect;

}



