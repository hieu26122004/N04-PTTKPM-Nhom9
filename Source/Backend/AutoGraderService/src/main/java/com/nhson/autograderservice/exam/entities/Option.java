package com.nhson.autograderservice.exam.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Embeddable
@Setter
public class Option {
    @Column(name = "option_label") // You can specify the column name if needed
    private String label;

    @Column(name = "option_content") // You can specify the column name if needed
    private String content;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    public Option(String label, String content,Boolean isCorrect) {
        this.label = label;
        this.content = content;
        this.isCorrect = isCorrect;
    }
}

