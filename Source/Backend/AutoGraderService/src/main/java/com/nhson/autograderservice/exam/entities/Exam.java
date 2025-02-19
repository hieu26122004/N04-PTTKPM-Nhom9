package com.nhson.autograderservice.exam.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "exams")
@NoArgsConstructor
public class Exam {
    @Id
    private String examId;
    @Column(name = "exam_name")
    private String name;
    @Column(name = "class_id")
    private String classId;
    @Enumerated(EnumType.STRING)
    private Subject subject;
    private Integer duration;
    private String provider;
    @Column(name = "number_of_question")
    private Integer numberOfQuestion;
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdatedDate;
    private LocalDateTime due;
    private Integer maxAttempts;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private Type type;
}
