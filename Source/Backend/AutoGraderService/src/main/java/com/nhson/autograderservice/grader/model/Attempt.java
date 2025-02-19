package com.nhson.autograderservice.grader.model;

import com.nhson.autograderservice.exam.entities.Question;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;

import java.util.Date;
import java.util.List;


@Entity
@Table(name = "attempts")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Attempt implements Persistable<String> {
    @Id
    @Column(name = "attempt_id", nullable = false)
    private String attemptId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Embedded
    private Result result;

    @Transient
    private List<Question> questions;

    @ElementCollection
    @CollectionTable(name = "attempt_answers", joinColumns = @JoinColumn(name = "attempt_id"))
    private List<Answer> userAnswer;

    @Column(name = "exam_id", nullable = false)
    private String examId;
    @Column(name = "exam_name", nullable = false)
    private String examName;

    @Column(name = "total_time")
    private Integer totalTime;
    @CreatedDate
    private Date timestamp;

    @Transient
    private Boolean isNew = false;

    @Override
    public String getId() {
        return this.attemptId;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    public Attempt(String attemptId, String userId, Result result, List<Question> questions, List<Answer> userAnswer, String examId, Integer totalTime) {
        if (attemptId == null || attemptId.isEmpty()) {
            this.attemptId = java.util.UUID.randomUUID().toString();
            this.isNew = true;
        } else {
            this.attemptId = attemptId;
            this.isNew = false;
        }
        this.userId = userId;
        this.result = result;
        this.questions = questions;
        this.userAnswer = userAnswer;
        this.examId = examId;
        this.totalTime = totalTime;
    }

    public void calculateResult() {
        int correctCount = 0;
        int incorrectCount = 0;
        int unansweredCount = 0;
        for (Answer answer : userAnswer) {
            if (answer.getIsCorrect()) {
                correctCount++;
            } else if (answer.getUserAnswer() == null) {
                unansweredCount++;
            } else {
                incorrectCount++;
            }
        }
        result.setCorrectCount(correctCount);
        result.setIncorrectCount(incorrectCount);
        result.setUnansweredCount(unansweredCount);
        result.setScore((double) ((correctCount * 100) / questions.size()));
    }
}
