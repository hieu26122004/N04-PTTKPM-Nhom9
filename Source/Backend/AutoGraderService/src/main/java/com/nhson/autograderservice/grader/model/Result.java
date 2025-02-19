package com.nhson.autograderservice.grader.model;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Result {
    private Double score;

    @Column(name = "correct_count")
    private int correctCount;

    @Column(name = "incorrect_count")
    private int incorrectCount;

    @Column(name = "unanswered_count")
    private int unansweredCount;
}
