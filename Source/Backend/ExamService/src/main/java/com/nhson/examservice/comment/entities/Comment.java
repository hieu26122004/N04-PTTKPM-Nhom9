package com.nhson.examservice.comment.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document
@Data
public class Comment {
    @Id
    private String id;
    private String author;
    private String examId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private String parentId;
    private int replies;
    private boolean deleted;
}
