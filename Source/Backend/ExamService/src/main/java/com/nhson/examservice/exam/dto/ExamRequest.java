package com.nhson.examservice.exam.dto;

import com.nhson.examservice.exam.entities.DifficultyLevel;
import com.nhson.examservice.exam.entities.Subject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExamRequest {
    private String id;
    private String name;
    private Subject subject;
    private Integer duration;
    private String provider;
    private Integer numberOfQuestion;
    private DifficultyLevel difficultyLevel;
    private LocalDateTime due;
    private Integer maxAttempts;
    private String classId;
    public ExamRequest(String id,String name, Subject subject, Integer duration, String provider, Integer numberOfQuestion, DifficultyLevel difficultyLevel, Integer maxAttempts,String classId,LocalDateTime due) {
        this.name = name;
        this.subject = subject;
        this.duration = duration;
        this.provider = provider;
        this.numberOfQuestion = numberOfQuestion;
        this.difficultyLevel = difficultyLevel;
        this.maxAttempts = maxAttempts;
        this.due = due;
        this.id = id;
        this.classId = classId;
    }
}
