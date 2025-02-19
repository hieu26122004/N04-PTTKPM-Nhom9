package com.nhson.autograderservice.exam.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "questions")
public class Question {
    private String examId;

    @Id
    @Column(name = "question_id", nullable = false)
    private String questionId;

    @Lob
    private String content;

    @ElementCollection
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    private List<Option> options;

    @Column(name = "question_order")
    private Integer questionOrder;


    public Option getCorrectOption(){
        for(Option option : options) {
            if(option.getIsCorrect()) {
                return option;
            }
        }
        return null;
    }
}
