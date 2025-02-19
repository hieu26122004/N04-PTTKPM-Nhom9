package com.nhson.examservice.vote.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nhson.examservice.exam.entities.Exam;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exam_id", referencedColumnName = "examId")
    @JsonBackReference
    private Exam exam;

    private String userId;

    @Enumerated(EnumType.STRING)
    private VoteType voteType;
}
