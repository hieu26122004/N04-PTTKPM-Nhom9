package com.nhson.examservice.question.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.Persistable;

import java.util.List;

import static jakarta.persistence.CascadeType.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "questions")
public class Question implements Persistable<String> {

    private String examId;

    @Id
    @GenericGenerator(name = "custom-question-id", strategy = "com.nhson.examservice.question.id.CustomQuestionIdGenerator")
    @GeneratedValue(generator = "custom-question-id")
    private String questionId;
    @Lob
    private String content;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Cascade(value = org.hibernate.annotations.CascadeType.MERGE)
    private List<Option> options;
    @Column(name = "question_order")
    private Integer questionOrder;
    @Transient
    private boolean isNew = true;

    @Override
    public String getId() {
        return this.questionId;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }
    public void setOptions(List<Option> options) {
        this.options = options;
    }
}