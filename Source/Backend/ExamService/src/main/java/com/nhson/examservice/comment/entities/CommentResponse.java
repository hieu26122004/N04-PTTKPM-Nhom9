package com.nhson.examservice.comment.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private String id;
    private String author;
    private String examId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private List<CommentResponse> replies;
    private boolean deleted;
}
